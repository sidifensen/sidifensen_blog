package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.AddFavoriteDto;
import com.sidifensen.domain.entity.Favorite;
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
}
