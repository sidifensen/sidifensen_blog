package com.sidifensen.service.impl;

import com.sidifensen.domain.entity.Article;
import com.sidifensen.domain.enums.VisibleRangeEnum;
import com.sidifensen.service.ArticleAccessService;
import com.sidifensen.service.FollowService;
import com.sidifensen.service.VipService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 文章访问鉴权实现，统一收口粉丝可见和 VIP 可见的判断逻辑。
 */
@Service
public class ArticleAccessServiceImpl implements ArticleAccessService {

    @Resource
    private FollowService followService;

    @Resource
    private VipService vipService;

    /**
     * 详情页和列表页都复用这里，避免不同入口出现鉴权分叉。
     */
    @Override
    public boolean canAccessArticle(Article article, Integer currentUserId) {
        if (article == null) {
            return false;
        }
        Integer safeCurrentUserId = currentUserId == null ? 0 : currentUserId;
        if (Objects.equals(article.getUserId(), safeCurrentUserId) && safeCurrentUserId != 0) {
            return true;
        }
        Integer visibleRange = article.getVisibleRange();
        if (Objects.equals(visibleRange, VisibleRangeEnum.ALL.getCode())) {
            return true;
        }
        if (Objects.equals(visibleRange, VisibleRangeEnum.ME.getCode())) {
            return false;
        }
        if (Objects.equals(visibleRange, VisibleRangeEnum.FANS.getCode())) {
            return canAccessFansContent(article.getUserId(), safeCurrentUserId);
        }
        if (Objects.equals(visibleRange, VisibleRangeEnum.VIP.getCode())) {
            return canAccessVipContent(article.getUserId(), safeCurrentUserId);
        }
        return false;
    }

    /**
     * 粉丝可见要求当前用户已登录，且已关注作者本人。
     */
    @Override
    public boolean canAccessFansContent(Integer authorId, Integer currentUserId) {
        if (authorId == null || currentUserId == null || currentUserId <= 0) {
            return false;
        }
        if (Objects.equals(authorId, currentUserId)) {
            return true;
        }
        return Boolean.TRUE.equals(followService.isFollowing(currentUserId, authorId));
    }

    /**
     * VIP 可见默认允许作者本人访问，其余用户要求存在有效会员。
     */
    @Override
    public boolean canAccessVipContent(Integer authorId, Integer currentUserId) {
        if (authorId == null || currentUserId == null || currentUserId <= 0) {
            return false;
        }
        if (Objects.equals(authorId, currentUserId)) {
            return true;
        }
        return vipService.hasVipAccess(currentUserId);
    }
}
