package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.History;
import com.sidifensen.domain.vo.HistoryVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.HistoryMapper;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.service.HistoryService;
import com.sidifensen.utils.FingerprintUtils;
import com.sidifensen.utils.SecurityUtils;
import com.sidifensen.utils.WebUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Resource
    private HistoryMapper historyMapper;

    @Override
    public boolean checkAndRecordRead(Integer articleId, Integer userId, String ipAddress) {
        try {
            // 1. 生成访客指纹（仅用于未登录用户）
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
            String identifier = null;
            if (userId != null) {
                identifier = "user:" + userId;
            } else {
                identifier = "fp:" + (fingerprint != null ? fingerprint : "unknown");
            }

            // 如果Redis中已存在该标识，说明已浏览过
            if (redisComponent.hasReadArticle(articleId, identifier)) {
                // 对于登录用户，需要更新数据库中的浏览时间
                if (userId != null) {
                    updateViewTime(articleId, userId);
                }
                return false;
            }

            // 3. 区分登录用户和访客的处理逻辑
            if (userId != null) {
                // 登录用户：检查数据库中是否存在浏览记录
                long count = this.count(new LambdaQueryWrapper<History>()
                        .eq(History::getArticleId, articleId)
                        .eq(History::getUserId, userId));

                if (count > 0) {
                    // 数据库中存在记录，但Redis中不存在，重新加入Redis，并更新浏览时间
                    redisComponent.recordArticleRead(articleId, identifier);
                    updateViewTime(articleId, userId);
                    return false;
                }

                // 登录用户首次浏览：保存到数据库
                History history = new History()
                        .setArticleId(articleId)
                        .setUserId(userId);

                int saveResult = historyMapper.insert(history);
                if (saveResult <= 0) {
                    throw new BlogException(BlogConstants.SaveReadRecordError);
                }
            }

            // 4. 添加到Redis缓存（登录用户和访客都需要）
            redisComponent.recordArticleRead(articleId, identifier);

            // 5. 增加文章热度（用于热门文章统计）
            redisComponent.incrementArticleHotScore(articleId);

            return true;

        } catch (Exception e) {
            log.error("记录浏览失败，文章ID: {}, 用户ID: {}, IP: {}, 错误: {}", articleId, userId, ipAddress, e.getMessage(), e);
            throw new BlogException(BlogConstants.SaveReadRecordError);
        }
    }


    /**
     * 更新登录用户的浏览时间
     * @param articleId 文章ID
     * @param userId    用户ID
     */
    private void updateViewTime(Integer articleId, Integer userId) {
        try {
            boolean updateResult = this.lambdaUpdate()
                    .eq(History::getArticleId, articleId)
                    .eq(History::getUserId, userId)
                    .update(new History());

            if (!updateResult) {
                log.warn("更新浏览时间失败，文章ID: {}, 用户ID: {}", articleId, userId);
            }
        } catch (Exception e) {
            log.error("更新浏览时间异常，文章ID: {}, 用户ID: {}, 错误: {}", articleId, userId, e.getMessage(), e);
        }
    }

    @Override
    public PageVo<List<HistoryVo>> getUserHistoryList(Integer pageNum, Integer pageSize) {
        try {
            // 获取当前登录用户ID
            Integer userId = SecurityUtils.getUserId();

            // 创建分页对象
            Page<HistoryVo> page = new Page<>(pageNum, pageSize);

            // 执行分页查询，使用自定义SQL一次性获取所有关联数据
            List<HistoryVo> historyList = historyMapper.getUserHistoryList(page, userId);

            // 返回分页结果
            return new PageVo<>(historyList, page.getTotal());

        } catch (Exception e) {
            log.error("获取用户浏览历史失败，页码: {}, 页面大小: {}, 错误: {}", pageNum, pageSize, e.getMessage(), e);
            throw new BlogException(BlogConstants.GetUserHistoryError);
        }
    }

    @Override
    public int clearUserHistory() {
        try {
            // 获取当前登录用户ID
            Integer userId = SecurityUtils.getUserId();

            // 删除当前用户的所有浏览记录，直接返回删除的记录数量
            int deletedCount = historyMapper.delete(new LambdaQueryWrapper<History>()
                    .eq(History::getUserId, userId));

            return deletedCount;

        } catch (Exception e) {
            log.error("清除用户浏览历史失败，错误: {}", e.getMessage(), e);
            throw new BlogException(BlogConstants.ClearUserHistoryError);
        }
    }

}
