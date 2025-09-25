package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.AddFavoriteDto;
import com.sidifensen.domain.entity.Article;
import com.sidifensen.domain.entity.ArticleFavorite;
import com.sidifensen.domain.entity.Favorite;
import com.sidifensen.domain.vo.ArticleVo;
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
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<Integer> collectedFavoriteIds = articleFavoriteList.stream()
                .map(ArticleFavorite::getFavoriteId)
                .collect(Collectors.toSet());

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

    @Override
    public List<FavoriteVo> getFavoriteListByUserId(Integer userId) {
        // 获取当前用户ID
        Integer currentUserId = SecurityUtils.getUserId();

        // 如果没有传入userId，则使用当前用户ID
        Integer targetUserId = userId != null ? userId : currentUserId;

        // 构建查询条件
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, targetUserId);

        // 如果查询的是别人的收藏夹，只返回公开的
        if (!targetUserId.equals(currentUserId)) {
            queryWrapper.eq(Favorite::getShowStatus, 0); // 0-公开
        }

        queryWrapper.orderByDesc(Favorite::getCreateTime);

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
    public List<ArticleVo> getArticleListByFavoriteId(Integer favoriteId) {
        // 检查收藏夹是否存在
        Favorite favorite = this.getById(favoriteId);
        if (favorite == null) {
            throw new BlogException(BlogConstants.NotFoundFavorite);
        }

        // 获取当前用户ID
        Integer currentUserId = SecurityUtils.getUserId();

        // 如果不是当前用户的收藏夹且收藏夹是私密的，则报错
        if (!favorite.getUserId().equals(currentUserId) && favorite.getShowStatus() == 1) {
            throw new BlogException(BlogConstants.CannotAccessPrivateFavorite);
        }

        // 查询收藏夹中的文章ID列表
        LambdaQueryWrapper<ArticleFavorite> articleFavoriteQueryWrapper = new LambdaQueryWrapper<>();
        articleFavoriteQueryWrapper.eq(ArticleFavorite::getFavoriteId, favoriteId)
                .orderByDesc(ArticleFavorite::getCreateTime);

        List<ArticleFavorite> articleFavoriteList = articleFavoriteMapper.selectList(articleFavoriteQueryWrapper);

        if (articleFavoriteList.isEmpty()) {
            return List.of();
        }

        // 提取文章ID列表
        List<Integer> articleIds = articleFavoriteList.stream()
                .map(ArticleFavorite::getArticleId)
                .toList();

        // 查询文章详情
        LambdaQueryWrapper<Article> articleQueryWrapper = new LambdaQueryWrapper<>();
        articleQueryWrapper.in(Article::getId, articleIds);
        List<Article> articles = articleMapper.selectList(articleQueryWrapper);

        // 转换为ArticleVo
        List<ArticleVo> articleVoList = articles.stream()
                .map(article -> BeanUtil.copyProperties(article, ArticleVo.class))
                .toList();

        return articleVoList;
    }
}
