package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.CommentAuditDto;
import com.sidifensen.domain.dto.CommentDto;
import com.sidifensen.domain.dto.CommentFilterDto;
import com.sidifensen.domain.dto.CommentSearchDto;
import com.sidifensen.domain.entity.Comment;
import com.sidifensen.domain.vo.AdminCommentVo;
import com.sidifensen.domain.vo.CommentVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.UserCommentManageVo;

import java.util.List;

/**
 * 评论服务接口
 *
 * @author sidifensen
 * @since 2025-09-15
 */
public interface CommentService extends IService<Comment> {

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
     * @return 评论列表
     */
    List<AdminCommentVo> adminGetCommentList();

    /**
     * 管理员根据用户ID获取评论列表
     *
     * @param userId 用户ID
     * @return 用户评论列表
     */
    List<AdminCommentVo> adminGetCommentsByUserId(Integer userId);

    /**
     * 管理员搜索评论
     *
     * @param commentSearchDto 搜索条件
     * @return 搜索结果
     */
    List<AdminCommentVo> adminSearchComment(CommentSearchDto commentSearchDto);

    /**
     * 管理员审核评论
     *
     * @param commentAuditDto 评论审核信息
     */
    void adminExamineComment(CommentAuditDto commentAuditDto);

    /**
     * 管理员批量审核评论
     *
     * @param commentAuditDtos 评论审核信息列表
     */
    void adminExamineBatchComment(List<CommentAuditDto> commentAuditDtos);

    /**
     * 管理员删除评论
     *
     * @param commentId 评论id
     */
    void adminDeleteComment(Integer commentId);

    /**
     * 管理员批量删除评论
     *
     * @param commentIds 评论ID列表
     */
    void adminDeleteBatchComment(List<Integer> commentIds);

    /**
     * 获取评论总数统计
     *
     * @return 评论总数
     */
    Long getCommentTotalCount();

    /**
     * 获取用户评论列表(评论管理)
     *
     * @param pageNum          页码
     * @param pageSize         页大小
     * @param commentFilterDto 评论筛选条件
     * @return 用户评论列表
     */
    PageVo<List<UserCommentManageVo>> getUserCommentManageList(Integer pageNum, Integer pageSize, CommentFilterDto commentFilterDto);

}
