package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.History;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.HistoryMapper;
import com.sidifensen.service.HistoryService;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.utils.FingerprintUtils;
import com.sidifensen.utils.WebUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 浏览历史服务实现类
 *
 * @author sidifensen
 * @since 2025-09-19
 */
@Service
@Slf4j
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, History> implements HistoryService {

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private FingerprintUtils fingerprintUtils;

    @Override
    @Transactional
    public boolean checkAndRecordRead(Integer articleId, Integer userId, String ipAddress) {
        try {
            // 1. 生成访客指纹
            String fingerprint = null;
            if (userId == null) {
                // 未登录用户，生成指纹
                try {
                    fingerprint = fingerprintUtils.generateFingerprint(WebUtils.getRequest());
                } catch (Exception e) {
                    fingerprint = "ip_" + ipAddress;
                }
            }

            // 2. 先检查Redis缓存，快速判断是否已浏览
            String identifier = getUserIdentifier(userId, fingerprint);

            // 如果Redis中已存在该标识，说明已浏览过
            if (redisComponent.hasReadArticle(articleId, identifier)) {
                return false;
            }

            // 3. 检查数据库中是否存在浏览记录（Redis可能已过期）
            long count;
            if (userId != null) {
                // 登录用户：通过用户ID查询
                count = this.count(new LambdaQueryWrapper<History>()
                        .eq(History::getArticleId, articleId)
                        .eq(History::getUserId, userId));
            } else {
                // 未登录用户：通过指纹查询（优先）或IP查询（备选）
                count = this.count(new LambdaQueryWrapper<History>()
                        .eq(History::getArticleId, articleId)
                        .eq(History::getFingerprint, fingerprint));

                if (count == 0 && fingerprint != null && fingerprint.startsWith("ip_")) {
                    // 如果指纹查询无结果且是IP备选方案，再用IP查询一次
                    count = this.count(new LambdaQueryWrapper<History>()
                            .eq(History::getArticleId, articleId)
                            .eq(History::getIpAddress, ipAddress)
                            .isNull(History::getUserId));
                }
            }

            if (count > 0) {
                // 数据库中存在记录，但Redis中不存在，重新加入Redis
                redisComponent.recordArticleRead(articleId, identifier);
                log.debug("数据库中存在浏览记录，重新加入Redis缓存");
                return false;
            }

            // 4. 用户/访客未浏览过，创建浏览记录
            History history = new History()
                    .setArticleId(articleId)
                    .setUserId(userId)
                    .setIpAddress(ipAddress)
                    .setFingerprint(fingerprint)
                    .setViewTime(new Date());

            // 5. 保存到数据库
            boolean saveResult = this.save(history);
            if (!saveResult) {
                throw new BlogException(BlogConstants.SaveReadRecordError);
            }

            // 6. 添加到Redis缓存
            redisComponent.recordArticleRead(articleId, identifier);

            log.info("成功记录用户/访客 {} 对文章 {} 的浏览", identifier, articleId);
            return true;

        } catch (Exception e) {
            log.error("记录浏览失败，文章ID: {}, 用户ID: {}, IP: {}, 错误: {}",
                    articleId, userId, ipAddress, e.getMessage(), e);
            throw new BlogException(BlogConstants.SaveReadRecordError);
        }
    }

    /**
     * 获取用户标识符
     * 登录用户使用用户ID，未登录用户使用浏览器指纹
     *
     * @param userId      用户ID
     * @param fingerprint 浏览器指纹
     * @return 用户标识符
     */
    private String getUserIdentifier(Integer userId, String fingerprint) {
        if (userId != null) {
            return "user:" + userId;
        } else {
            return "fp:" + (fingerprint != null ? fingerprint : "unknown");
        }
    }
}
