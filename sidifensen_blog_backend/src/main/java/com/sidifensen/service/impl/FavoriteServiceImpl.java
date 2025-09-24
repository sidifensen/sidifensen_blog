package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.AddFavoriteDto;
import com.sidifensen.domain.entity.Article;
import com.sidifensen.domain.entity.ArticleFavorite;
import com.sidifensen.domain.entity.Favorite;
import com.sidifensen.domain.vo.FavoriteVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.ArticleFavoriteMapper;
import com.sidifensen.mapper.ArticleMapper;
import com.sidifensen.mapper.FavoriteMapper;
import com.sidifensen.service.FavoriteService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-09-16
 */
@Service
@Slf4j
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private ArticleFavoriteMapper articleFavoriteMapper;


    @Override
    public void addFavorite(AddFavoriteDto addFavoriteDto) {
        // 创建收藏夹实体
        Favorite favorite = new Favorite();
        BeanUtils.copyProperties(addFavoriteDto, favorite);

        // 设置用户ID
        Integer userId = SecurityUtils.getUserId();
        favorite.setUserId(userId);

        boolean result = favoriteMapper.insert(favorite) > 0;
        if (!result) {
            throw new BlogException(BlogConstants.AddFavoriteError);
        }

    }

    @Override
    public void addArticleToFavorite(Integer articleId, Integer favoriteId) {
        // 校验参数
        if (articleId == null) {
            throw new BlogException(BlogConstants.ArticleIdRequired);
        }
        if (favoriteId == null) {
            throw new BlogException(BlogConstants.FavoriteIdRequired);
        }

        // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BlogException(BlogConstants.NotFoundArticle);
        }

        // 检查收藏夹是否存在
        Favorite favorite = this.getById(favoriteId);
        if (favorite == null) {
            throw new BlogException(BlogConstants.NotFoundFavorite);
        }

        // 检查收藏夹是否属于当前用户
        Integer currentUserId = SecurityUtils.getUserId();
        if (!favorite.getUserId().equals(currentUserId)) {
            throw new BlogException(BlogConstants.CannotHandleOthersFavorite);
        }

        // 检查文章是否已在该收藏夹中
        LambdaQueryWrapper<ArticleFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleFavorite::getArticleId, articleId)
                .eq(ArticleFavorite::getFavoriteId, favoriteId);
        ArticleFavorite existingRelation = articleFavoriteMapper.selectOne(queryWrapper);
        if (existingRelation != null) {
            throw new BlogException(BlogConstants.ArticleAlreadyInFavorite);
        }

        // 创建文章-收藏夹关联记录
        ArticleFavorite articleFavorite = new ArticleFavorite();
        articleFavorite.setArticleId(articleId);
        articleFavorite.setFavoriteId(favoriteId);

        boolean saveResult = articleFavoriteMapper.insert(articleFavorite) > 0;
        if (!saveResult) {
            throw new BlogException(BlogConstants.AddArticleToFavoriteError);
        }

        // 增加收藏夹的文章数量
        favorite.setArticleCount(favorite.getArticleCount() + 1);
        boolean updateResult = favoriteMapper.updateById(favorite) > 0;
        if (!updateResult) {
            throw new BlogException(BlogConstants.UpdateFavoriteArticleCountError);
        }

    }

    @Override
    public void removeArticleFromFavorite(Integer articleId, Integer favoriteId) {
        // 校验参数
        if (articleId == null) {
            throw new BlogException(BlogConstants.ArticleIdRequired);
        }
        if (favoriteId == null) {
            throw new BlogException(BlogConstants.FavoriteIdRequired);
        }

        // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BlogException(BlogConstants.NotFoundArticle);
        }

        // 检查收藏夹是否存在
        Favorite favorite = this.getById(favoriteId);
        if (favorite == null) {
            throw new BlogException(BlogConstants.NotFoundFavorite);
        }

        // 检查收藏夹是否属于当前用户
        Integer currentUserId = SecurityUtils.getUserId();
        if (!favorite.getUserId().equals(currentUserId)) {
            throw new BlogException(BlogConstants.CannotHandleOthersFavorite);
        }

        // 检查文章是否在该收藏夹中
        LambdaQueryWrapper<ArticleFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleFavorite::getArticleId, articleId)
                .eq(ArticleFavorite::getFavoriteId, favoriteId);
        ArticleFavorite existingRelation = articleFavoriteMapper.selectOne(queryWrapper);
        if (existingRelation == null) {
            throw new BlogException(BlogConstants.ArticleNotInFavorite);
        }

        // 删除文章-收藏夹关联记录
        boolean deleteResult = articleFavoriteMapper.deleteById(existingRelation.getId()) > 0;
        if (!deleteResult) {
            throw new BlogException(BlogConstants.RemoveArticleFromFavoriteError);
        }

        // 减少收藏夹的文章数量
        if (favorite.getArticleCount() > 0) {
            favorite.setArticleCount(favorite.getArticleCount() - 1);
            boolean updateResult = favoriteMapper.updateById(favorite) > 0;
            if (!updateResult) {
                throw new BlogException(BlogConstants.UpdateFavoriteArticleCountError);
            }
        }

    }

    @Override
    public List<FavoriteVo> getCurrentUserFavoriteList() {
        // 获取当前用户ID
        Integer currentUserId = SecurityUtils.getUserId();
        
        // 查询当前用户的收藏夹列表
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, currentUserId)
                .orderByDesc(Favorite::getCreateTime);
        
        List<Favorite> favoriteList = favoriteMapper.selectList(queryWrapper);
        
        // 转换为VO对象
        return favoriteList.stream()
                .map(favorite -> {
                    FavoriteVo favoriteVo = new FavoriteVo();
                    BeanUtils.copyProperties(favorite, favoriteVo);
                    return favoriteVo;
                })
                .toList();
    }

    @Override
    public List<FavoriteVo> getFavoriteListByArticleId(Integer articleId) {
        // 校验参数
        if (articleId == null) {
            throw new BlogException(BlogConstants.ArticleIdRequired);
        }

        // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BlogException(BlogConstants.NotFoundArticle);
        }

        // 获取当前用户ID
        Integer currentUserId = SecurityUtils.getUserId();
        
        // 查询当前用户的收藏夹列表
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, currentUserId)
                .orderByDesc(Favorite::getCreateTime);
        
        List<Favorite> favoriteList = favoriteMapper.selectList(queryWrapper);
        
        // 查询该文章在哪些收藏夹中被收藏
        LambdaQueryWrapper<ArticleFavorite> articleFavoriteQueryWrapper = new LambdaQueryWrapper<>();
        articleFavoriteQueryWrapper.eq(ArticleFavorite::getArticleId, articleId);
        List<ArticleFavorite> articleFavoriteList = articleFavoriteMapper.selectList(articleFavoriteQueryWrapper);
        
        // 将文章收藏关系转换为Set，便于快速查找
        java.util.Set<Integer> collectedFavoriteIds = articleFavoriteList.stream()
                .map(ArticleFavorite::getFavoriteId)
                .collect(java.util.stream.Collectors.toSet());
        
        // 转换为VO对象并设置收藏状态
        return favoriteList.stream()
                .map(favorite -> {
                    FavoriteVo favoriteVo = new FavoriteVo();
                    BeanUtils.copyProperties(favorite, favoriteVo);
                    // 设置是否被该收藏夹收藏
                    favoriteVo.setIsCollected(collectedFavoriteIds.contains(favorite.getId()));
                    return favoriteVo;
                })
                .toList();
    }
}
