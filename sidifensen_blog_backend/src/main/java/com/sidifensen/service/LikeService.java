package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.entity.Like;

/**
 * <p>
 * 点赞服务类
 * </p>
 *
 * @author
 * @since 2025-09-15
 */
public interface LikeService extends IService<Like> {

    /**
     * 点赞或取消点赞
     *
     * @param type   点赞类型 0-文章 1-评论
     * @param typeId 点赞类型id
     */
    void toggleLike(Integer type, Integer typeId);

    /**
     * 检查当前登录用户是否已点赞
     *
     * @param type   点赞类型 0-文章 1-评论
     * @param typeId 点赞类型id
     * @return 是否已点赞
     */
    Boolean isLiked(Integer type, Integer typeId);

    /**
     * 获取点赞数量
     *
     * @param type   点赞类型 0-文章 1-评论
     * @param typeId 点赞类型id
     * @return 点赞数量
     */
    Long getLikeCount(Integer type, Integer typeId);

}
