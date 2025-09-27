package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.AddFavoriteDto;
import com.sidifensen.domain.dto.UpdateFavoriteDto;
import com.sidifensen.domain.entity.Favorite;
import com.sidifensen.domain.vo.ArticleVo;
import com.sidifensen.domain.vo.FavoriteVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-09-16
 */
public interface FavoriteService extends IService<Favorite> {

    /**
     * 新增收藏夹
     * 
     * @param addFavoriteDto 收藏夹信息
     */
    void addFavorite(AddFavoriteDto addFavoriteDto);

    /**
     * 更新收藏夹
     * 
     * @param updateFavoriteDto 收藏夹信息
     */
    void updateFavorite(UpdateFavoriteDto updateFavoriteDto);

    /**
     * 根据articleId把文章添加到文章-收藏夹关联表，并增加收藏夹的文章数量
     * 
     * @param articleId 文章ID
     * @param favoriteId 收藏夹ID
     */
    void addArticleToFavorite(Integer articleId, Integer favoriteId);

    /**
     * 根据articleId把文章从文章-收藏夹关联表中移除，并减少收藏夹的文章数量
     * 
     * @param articleId 文章ID
     * @param favoriteId 收藏夹ID
     */
    void removeArticleFromFavorite(Integer articleId, Integer favoriteId);

    /**
     * 获取当前用户的收藏夹列表
     * 
     * @return 收藏夹列表
     */
    List<FavoriteVo> getCurrentUserFavoriteList();

    /**
     * 根据文章ID获取当前用户的收藏夹列表，并标识该文章在各收藏夹中的收藏状态
     * 
     * @param articleId 文章ID
     * @return 收藏夹列表（包含收藏状态）
     */
    List<FavoriteVo> getFavoriteListByArticleId(Integer articleId);

    /**
     * 根据用户ID获取收藏夹列表
     * 如果userId为空则获取当前用户的收藏夹
     * 如果是查询别人的收藏夹，只返回公开的
     * 如果是查询当前用户的收藏夹，公开私密都返回
     * 
     * @param userId 用户ID，可以为空
     * @return 收藏夹列表
     */
    List<FavoriteVo> getFavoriteListByUserId(Integer userId);

    /**
     * 根据收藏夹ID获取收藏夹中的文章列表
     * 如果不是当前用户的收藏夹且收藏夹是私密的，则抛出异常
     * 
     * @param favoriteId 收藏夹ID
     * @return 文章列表
     */
    List<ArticleVo> getArticleListByFavoriteId(Integer favoriteId);
}
