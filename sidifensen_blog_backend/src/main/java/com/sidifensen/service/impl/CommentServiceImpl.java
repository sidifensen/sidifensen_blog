package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.CommentDto;
import com.sidifensen.domain.entity.Article;
import com.sidifensen.domain.entity.Comment;
import com.sidifensen.domain.vo.CommentVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.ArticleMapper;
import com.sidifensen.mapper.CommentMapper;
import com.sidifensen.service.ICommentService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论服务实现类
 *
 * @author sidifensen
 * @since 2025-09-15
 */
@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Override
    @Transactional
    public Integer addComment(CommentDto commentDto) {
        Integer currentUserId = SecurityUtils.getUserId();

        // 验证父评论是否存在
        if (commentDto.getParentId() != null && commentDto.getParentId() > 0) {
            Comment parentComment = getById(commentDto.getParentId());
            if (parentComment == null || parentComment.getIsDeleted() == 1) {
                throw new BlogException(BlogConstants.NotFoundParentComment);
            }

            // 如果父评论的审核状态不是通过，不允许回复
            if (parentComment.getExamineStatus() != 1) {
                throw new BlogException(BlogConstants.CannotReplyUnApprovedComment);
            }
        }

        // 创建评论实体
        Comment comment = new Comment();
        BeanUtil.copyProperties(commentDto, comment);
        comment.setUserId(currentUserId);

        // 保存评论
        boolean saved = save(comment);
        if (!saved) {
            throw new BlogException(BlogConstants.AddCommentError);
        }

        // 如果是回复评论，更新父评论的回复数
        if (commentDto.getParentId() != null && commentDto.getParentId() > 0) {
            LambdaUpdateWrapper<Comment> replyUpdateWrapper = new LambdaUpdateWrapper<>();
            replyUpdateWrapper.eq(Comment::getId, commentDto.getParentId())
                    .setIncrBy(Comment::getReplyCount, 1);
            commentMapper.update(null, replyUpdateWrapper);
        }

        // 更新文章评论数（所有评论都增加文章评论数）
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, commentDto.getArticleId())
                .setIncrBy(Article::getCommentCount, 1);
        boolean updateResult = articleMapper.update(null, updateWrapper) > 0;
        if (!updateResult) {
            log.warn("更新文章评论数失败，文章ID：{}", commentDto.getArticleId());
        }

        return comment.getId();
    }

    @Override
    @Transactional
    public void deleteComment(Integer commentId) {
        Integer currentUserId = SecurityUtils.getUserId();

        Comment comment = getById(commentId);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BlogException(BlogConstants.NotFoundComment);
        }

        // 只能删除自己的评论
        if (!comment.getUserId().equals(currentUserId)) {
            throw new BlogException(BlogConstants.CannotDeleteOthersComment);
        }

        // 逻辑删除评论
        boolean deleted = removeById(commentId);
        if (!deleted) {
            throw new BlogException(BlogConstants.DeleteCommentError);
        }

        // 如果是回复评论，更新父评论的回复数
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            LambdaUpdateWrapper<Comment> replyUpdateWrapper = new LambdaUpdateWrapper<>();
            replyUpdateWrapper.eq(Comment::getId, comment.getParentId())
                    .setIncrBy(Comment::getReplyCount, -1);
            commentMapper.update(null, replyUpdateWrapper);
        }

        // 更新文章评论数（所有评论都减少文章评论数）
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, comment.getArticleId())
                .setIncrBy(Article::getCommentCount, -1);
        boolean updateResult = articleMapper.update(null, updateWrapper) > 0;
        if (!updateResult) {
            log.warn("更新文章评论数失败，文章ID：{}", comment.getArticleId());
        }
    }

    @Override
    public PageVo<List<CommentVo>> getCommentList(Integer articleId, Integer pageNum, Integer pageSize) {
        if (articleId == null || articleId <= 0) {
            throw new BlogException(BlogConstants.ArticleIdRequired);
        }

        Integer offset = (pageNum - 1) * pageSize;
        Integer currentUserId = SecurityUtils.getUserId() == 0 ? null : SecurityUtils.getUserId();

        // 获取顶级评论列表
        List<CommentVo> commentList = commentMapper.selectCommentListWithUserInfo(articleId, 0, offset, pageSize,
                currentUserId);

        // 为每个顶级评论加载部分回复（例如前3条）
        for (CommentVo comment : commentList) {
            List<CommentVo> replyList = commentMapper.selectCommentListWithUserInfo(articleId, comment.getId(), 0, 3,
                    currentUserId);
            comment.setChildren(replyList);
        }

        // 获取总数
        LambdaQueryWrapper<Comment> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(Comment::getArticleId, articleId)
                .eq(Comment::getParentId, 0)
                .eq(Comment::getExamineStatus, 1)
                .eq(Comment::getIsDeleted, 0);
        Long total = count(countWrapper);

        return new PageVo<>(commentList, total.longValue());
    }

    @Override
    public PageVo<List<CommentVo>> getReplyList(Integer commentId, Integer pageNum, Integer pageSize) {
        if (commentId == null || commentId <= 0) {
            throw new BlogException(BlogConstants.CommentIdRequired);
        }

        Comment parentComment = getById(commentId);
        if (parentComment == null || parentComment.getIsDeleted() == 1) {
            throw new BlogException(BlogConstants.NotFoundParentComment);
        }

        Integer offset = (pageNum - 1) * pageSize;
        Integer currentUserId = SecurityUtils.getUserId() == 0 ? null : SecurityUtils.getUserId();

        // 获取回复列表
        List<CommentVo> replyList = commentMapper.selectCommentListWithUserInfo(
                parentComment.getArticleId(), commentId, offset, pageSize, currentUserId);

        // 获取总数
        LambdaQueryWrapper<Comment> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(Comment::getArticleId, parentComment.getArticleId())
                .eq(Comment::getParentId, commentId)
                .eq(Comment::getExamineStatus, 1)
                .eq(Comment::getIsDeleted, 0);
        Long total = count(countWrapper);

        return new PageVo<>(replyList, total.longValue());
    }

    @Override
    @Transactional
    public void auditComment(Integer commentId, Integer examineStatus) {
        if (commentId == null || commentId <= 0) {
            throw new BlogException(BlogConstants.CommentIdRequired);
        }

        if (examineStatus == null || examineStatus < 0 || examineStatus > 2) {
            throw new BlogException(BlogConstants.CommentExamineStatusError);
        }

        Comment comment = getById(commentId);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BlogException(BlogConstants.NotFoundComment);
        }

        comment.setExamineStatus(examineStatus);
        boolean updated = updateById(comment);
        if (!updated) {
            throw new BlogException(BlogConstants.CommentAuditError);
        }

        log.info("管理员审核评论，评论ID：{}，审核状态：{}", commentId, examineStatus);
    }

    @Override
    @Transactional
    public void adminDeleteComment(Integer commentId) {
        if (commentId == null || commentId <= 0) {
            throw new BlogException(BlogConstants.CommentIdRequired);
        }

        Comment comment = getById(commentId);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BlogException(BlogConstants.NotFoundComment);
        }

        // 删除评论及其所有回复
        deleteCommentAndReplies(commentId);

        // 如果是回复评论，更新父评论的回复数
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            LambdaUpdateWrapper<Comment> replyUpdateWrapper = new LambdaUpdateWrapper<>();
            replyUpdateWrapper.eq(Comment::getId, comment.getParentId())
                    .setIncrBy(Comment::getReplyCount, -1);
            commentMapper.update(null, replyUpdateWrapper);
        }

        // 更新文章评论数（所有评论都减少文章评论数）
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, comment.getArticleId())
                .setIncrBy(Article::getCommentCount, -1);
        boolean updateResult = articleMapper.update(null, updateWrapper) > 0;
        if (!updateResult) {
            log.warn("更新文章评论数失败，文章ID：{}", comment.getArticleId());
        }

        log.info("管理员删除评论，评论ID：{}", commentId);
    }

    /**
     * 递归删除评论及其所有回复
     *
     * @param commentId 评论ID
     */
    private void deleteCommentAndReplies(Integer commentId) {
        // 查找所有子评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, commentId)
                .eq(Comment::getIsDeleted, 0);
        List<Comment> childComments = list(queryWrapper);

        // 递归删除子评论
        for (Comment childComment : childComments) {
            deleteCommentAndReplies(childComment.getId());
        }

        // 删除当前评论
        removeById(commentId);
    }
}
