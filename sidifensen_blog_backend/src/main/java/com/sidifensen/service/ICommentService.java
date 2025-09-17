package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.CommentDto;
import com.sidifensen.domain.entity.Comment;
import com.sidifensen.domain.vo.AdminCommentVo;
import com.sidifensen.domain.vo.CommentVo;
import com.sidifensen.domain.vo.PageVo;

import java.util.List;

/**
 * 评论服务接口
 *
 * @author sidifensen
 * @since 2025-09-15
 */
public interface ICommentService extends IService<Comment> {

    /**
     * 发表评论
     *
     * @param commentDto 评论信息
     * @return 评论id
     */
    Integer addComment(CommentDto commentDto);

    /**
     * 删除评论
     *
     * @param commentId 评论id
     */
    void deleteComment(Integer commentId);

    /**
     * 获取文章评论列表（分层结构）
     *
     * @param articleId 文章id
     * @param pageNum   页码
     * @param pageSize  页大小
     * @return 评论列表
     */
    PageVo<List<CommentVo>> getCommentList(Integer articleId, Integer pageNum, Integer pageSize);

    /**
     * 获取评论的回复列表
     *
     * @param commentId 评论id
     * @param pageNum   页码
     * @param pageSize  页大小
     * @return 回复列表
     */
    PageVo<List<CommentVo>> getReplyList(Integer commentId, Integer pageNum, Integer pageSize);

    /**
     * 管理员获取所有评论列表
     *
     * @param pageNum       页码
     * @param pageSize      页大小
     * @param examineStatus 审核状态（可选）0-待审核 1-审核通过 2-审核未通过
     * @param userId        用户id（可选）
     * @param articleId     文章id（可选）
     * @param keyword       关键词搜索（可选）
     * @return 评论列表
     */
    PageVo<List<AdminCommentVo>> adminGetCommentList(Integer pageNum, Integer pageSize,
                                                     Integer examineStatus, Integer userId,
                                                     Integer articleId, String keyword);

    /**
     * 管理员审核评论
     *
     * @param commentId     评论id
     * @param examineStatus 审核状态
     */
    void auditComment(Integer commentId, Integer examineStatus);

    /**
     * 管理员删除评论
     *
     * @param commentId 评论id
     */
    void adminDeleteComment(Integer commentId);

}
