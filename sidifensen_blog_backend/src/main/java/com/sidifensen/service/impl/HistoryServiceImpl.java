package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.Article;
import com.sidifensen.domain.entity.History;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.vo.HistoryVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.ArticleMapper;
import com.sidifensen.mapper.HistoryMapper;
import com.sidifensen.mapper.SysUserMapper;
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

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private SysUserMapper sysUserMapper;

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
                return false;
            }

            // 3. 区分登录用户和访客的处理逻辑
            if (userId != null) {
                // 登录用户：检查数据库中是否存在浏览记录
                long count = this.count(new LambdaQueryWrapper<History>()
                        .eq(History::getArticleId, articleId)
                        .eq(History::getUserId, userId));

                if (count > 0) {
                    // 数据库中存在记录，但Redis中不存在，重新加入Redis
                    redisComponent.recordArticleRead(articleId, identifier);
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

            return true;

        } catch (Exception e) {
            log.error("记录浏览失败，文章ID: {}, 用户ID: {}, IP: {}, 错误: {}", articleId, userId, ipAddress, e.getMessage(), e);
            throw new BlogException(BlogConstants.SaveReadRecordError);
        }
    }

    @Override
    public PageVo<List<HistoryVo>> getUserHistoryList(Integer pageNum, Integer pageSize) {
        try {
            // 获取当前登录用户ID
            Integer userId = SecurityUtils.getUserId();
            
            // 创建分页对象
            Page<History> page = new Page<>(pageNum, pageSize);
            
            // 查询用户浏览历史，按浏览时间倒序
            LambdaQueryWrapper<History> historyWrapper = new LambdaQueryWrapper<History>()
                    .eq(History::getUserId, userId)
                    .orderByDesc(History::getViewTime);
            
            // 执行分页查询
            Page<History> historyPage = this.page(page, historyWrapper);
            List<History> historyList = historyPage.getRecords();
            
            // 如果没有浏览历史，直接返回空结果
            if (historyList.isEmpty()) {
                return new PageVo<>(List.of(), 0L);
            }
            
            // 提取文章ID列表
            List<Integer> articleIds = historyList.stream()
                    .map(History::getArticleId)
                    .distinct()
                    .toList();
            
            // 批量查询文章信息（只查询已审核通过的文章）
            LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<Article>()
                    .in(Article::getId, articleIds)
                    .eq(Article::getExamineStatus, 1); // 1表示审核通过
            List<Article> articles = articleMapper.selectList(articleWrapper);
            
            // 提取作者ID列表
            List<Integer> authorIds = articles.stream()
                    .map(Article::getUserId)
                    .distinct()
                    .toList();
            
            // 批量查询作者信息
            final List<SysUser> authors;
            if (!authorIds.isEmpty()) {
                authors = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                        .in(SysUser::getId, authorIds));
            } else {
                authors = List.of();
            }
            
            // 构建VO结果列表
            List<HistoryVo> historyVoList = historyList.stream()
                    .map(history -> {
                        // 查找对应的文章
                        Article article = articles.stream()
                                .filter(a -> a.getId().equals(history.getArticleId()))
                                .findFirst()
                                .orElse(null);
                        
                        // 如果文章不存在或已被删除，跳过该历史记录
                        if (article == null) {
                            return null;
                        }
                        
                        // 查找对应的作者
                        SysUser author = authors.stream()
                                .filter(u -> u.getId().equals(article.getUserId()))
                                .findFirst()
                                .orElse(null);
                        
                        // 构建HistoryVo
                        HistoryVo historyVo = new HistoryVo();
                        historyVo.setId(history.getId());
                        historyVo.setArticleId(article.getId());
                        historyVo.setTitle(article.getTitle());
                        historyVo.setCoverUrl(article.getCoverUrl());
                        historyVo.setDescription(article.getDescription());
                        historyVo.setReadCount(article.getReadCount());
                        historyVo.setLikeCount(article.getLikeCount());
                        historyVo.setViewTime(history.getViewTime());
                        historyVo.setCreateTime(article.getCreateTime());
                        
                        // 设置作者信息
                        if (author != null) {
                            historyVo.setAuthorId(author.getId());
                            historyVo.setAuthorNickname(author.getNickname());
                            historyVo.setAuthorAvatar(author.getAvatar());
                        }
                        
                        return historyVo;
                    })
                    .filter(vo -> vo != null) // 过滤掉空的记录
                    .toList();
            
            // 返回分页结果
            return new PageVo<>(historyVoList, historyPage.getTotal());
            
        } catch (Exception e) {
            log.error("获取用户浏览历史失败，页码: {}, 页面大小: {}, 错误: {}", pageNum, pageSize, e.getMessage(), e);
            throw new BlogException(BlogConstants.GetUserHistoryError);
        }
    }

}
