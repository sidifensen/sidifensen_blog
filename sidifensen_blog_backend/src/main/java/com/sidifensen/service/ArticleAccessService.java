package com.sidifensen.service;

import com.sidifensen.domain.entity.Article;

/**
 * 文章可见范围鉴权服务，统一处理公开、粉丝、VIP 等访问判断。
 */
public interface ArticleAccessService {

    /**
     * 校验当前用户是否可以访问指定文章。
     */
    boolean canAccessArticle(Article article, Integer currentUserId);

    /**
     * 校验当前用户是否可以访问粉丝可见内容。
     */
    boolean canAccessFansContent(Integer authorId, Integer currentUserId);

    /**
     * 校验当前用户是否可以访问 VIP 可见内容。
     */
    boolean canAccessVipContent(Integer authorId, Integer currentUserId);
}
