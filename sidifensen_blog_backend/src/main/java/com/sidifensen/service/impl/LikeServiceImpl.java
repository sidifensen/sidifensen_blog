package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.Article;
import com.sidifensen.domain.entity.Comment;
import com.sidifensen.domain.entity.Like;
import com.sidifensen.domain.enums.LikeTypeEnum;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.LikeMapper;
import com.sidifensen.mapper.ArticleMapper;
import com.sidifensen.mapper.CommentMapper;
import com.sidifensen.service.LikeService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 点赞服务实现类
 * </p>
 *
 * @author
 * @since 2025-09-15
 */
@Service
@Slf4j
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CommentMapper commentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleLike(Integer type, Integer typeId) {
        Integer userId = SecurityUtils.getUserId();

        // 验证点赞类型
        LikeTypeEnum likeType = LikeTypeEnum.getByCode(type);
        if (likeType == null) {
            throw new BlogException(BlogConstants.LikeTypeError);
        }

        // 查询是否已点赞
        LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Like::getUserId, userId)
                .eq(Like::getType, type)
                .eq(Like::getTypeId, typeId);

        Like existingLike = this.getOne(queryWrapper);

        try {
            if (existingLike != null) {
                // 已点赞，执行取消点赞
                this.removeById(existingLike.getId());
                // 根据类型减少对应的点赞量
                if (LikeTypeEnum.ARTICLE.equals(likeType)) {
                    updateArticleLikeCount(typeId, -1);
                } else if (LikeTypeEnum.COMMENT.equals(likeType)) {
                    updateCommentLikeCount(typeId, -1);
                }
            } else {
                // 未点赞，执行点赞
                Like like = new Like();
                like.setUserId(userId);
                like.setType(type);
                like.setTypeId(typeId);
                this.save(like);
                // 根据类型增加对应的点赞量
                if (LikeTypeEnum.ARTICLE.equals(likeType)) {
                    updateArticleLikeCount(typeId, 1);
                } else if (LikeTypeEnum.COMMENT.equals(likeType)) {
                    updateCommentLikeCount(typeId, 1);
                }
            }
        } catch (Exception e) {
            log.error("用户{}切换点赞状态失败，类型:{}, ID:{}", userId, likeType.getDesc(), typeId, e);
            if (LikeTypeEnum.ARTICLE.equals(likeType)) {
                throw new BlogException(BlogConstants.UpdateArticleLikeCountError);
            } else if (LikeTypeEnum.COMMENT.equals(likeType)) {
                throw new BlogException(BlogConstants.UpdateCommentLikeCountError);
            }
            throw new BlogException(BlogConstants.LikeTypeError);
        }
    }

    /**
     * 更新文章点赞量
     *
     * @param articleId 文章ID
     * @param delta     变化量 (1表示增加，-1表示减少)
     */
    private void updateArticleLikeCount(Integer articleId, int delta) {
        // 验证文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BlogException(BlogConstants.NotFoundArticle);
        }

        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, articleId);

        if (delta > 0) {
            // 增加点赞量
            updateWrapper.setSql("like_count = like_count + " + delta);
        } else {
            // 减少点赞量，但不能小于0
            updateWrapper.setSql(
                    "like_count = CASE WHEN like_count + " + delta + " < 0 THEN 0 ELSE like_count + " + delta + " END");
        }

        int updated = articleMapper.update(null, updateWrapper);
        if (updated == 0) {
            throw new BlogException(BlogConstants.UpdateArticleLikeCountError);
        }
    }

    /**
     * 更新评论点赞量
     *
     * @param commentId 评论ID
     * @param delta     变化量 (1表示增加，-1表示减少)
     */
    private void updateCommentLikeCount(Integer commentId, int delta) {
        // 验证评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BlogException(BlogConstants.NotFoundComment);
        }

        LambdaUpdateWrapper<Comment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Comment::getId, commentId);

        if (delta > 0) {
            // 增加点赞量
            updateWrapper.setSql("like_count = like_count + " + delta);
        } else {
            // 减少点赞量，但不能小于0
            updateWrapper.setSql(
                    "like_count = CASE WHEN like_count + " + delta + " < 0 THEN 0 ELSE like_count + " + delta + " END");
        }

        int updated = commentMapper.update(null, updateWrapper);
        if (updated == 0) {
            throw new BlogException(BlogConstants.UpdateCommentLikeCountError);
        }
    }

    @Override
    public Boolean isLiked(Integer type, Integer typeId) {
        Integer userId = SecurityUtils.getUserId();
        if (userId == 0) {
            return false;
        }

        LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Like::getUserId, userId)
                .eq(Like::getType, type)
                .eq(Like::getTypeId, typeId);

        return this.count(queryWrapper) > 0;
    }

    @Override
    public Long getLikeCount(Integer type, Integer typeId) {
        LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Like::getType, type)
                .eq(Like::getTypeId, typeId);

        return this.count(queryWrapper);
    }

}
