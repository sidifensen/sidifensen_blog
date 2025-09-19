package com.sidifensen.controller;

import com.sidifensen.aspect.TimeConsuming;
import com.sidifensen.domain.dto.CommentAuditDto;
import com.sidifensen.domain.dto.CommentDto;
import com.sidifensen.domain.dto.CommentSearchDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.AdminCommentVo;
import com.sidifensen.domain.vo.CommentVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.service.CommentService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-09-15
 */
@RestController
@RequestMapping("/comment")
@TimeConsuming
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 发表评论
     *
     * @param commentDto 评论信息
     * @return 评论id
     */
    @PostMapping("/add")
    public Result<Integer> addComment(@Valid @RequestBody CommentDto commentDto) {
        Integer commentId = commentService.addComment(commentDto);
        return Result.success(commentId);
    }

    /**
     * 删除评论（用户删除自己的评论）
     *
     * @param commentId 评论id
     * @return 操作结果
     */
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(@PathVariable Integer commentId) {
        commentService.deleteComment(commentId);
        return Result.success();
    }

    /**
     * 获取文章评论列表
     *
     * @param articleId 文章id
     * @param pageNum   页码
     * @param pageSize  页大小
     * @return 评论列表
     */
    @GetMapping("/list")
    public Result<PageVo<List<CommentVo>>> getCommentList(@NotNull Integer articleId, @NotNull Integer pageNum, @NotNull Integer pageSize) {
        PageVo<List<CommentVo>> commentList = commentService.getCommentList(articleId, pageNum, pageSize);
        return Result.success(commentList);
    }

    /**
     * 获取评论的回复列表
     *
     * @param commentId 评论id
     * @param pageNum   页码
     * @param pageSize  页大小
     * @return 回复列表
     */
    @GetMapping("/reply/list")
    public Result<PageVo<List<CommentVo>>> getReplyList(@NotNull Integer commentId, @NotNull Integer pageNum, @NotNull Integer pageSize) {
        PageVo<List<CommentVo>> replyList = commentService.getReplyList(commentId, pageNum, pageSize);
        return Result.success(replyList);
    }

    /**
     * 管理员获取所有评论列表
     *
     * @return 评论列表
     */
    @PreAuthorize("hasAuthority('comment:list')")
    @GetMapping("/admin/list")
    public Result<List<AdminCommentVo>> adminGetCommentList() {
        List<AdminCommentVo> commentList = commentService.adminGetCommentList();
        return Result.success(commentList);
    }

    /**
     * 管理员根据用户ID获取评论列表
     *
     * @param userId 用户ID
     * @return 用户评论列表
     */
    @PreAuthorize("hasAuthority('comment:user:list')")
    @GetMapping("/admin/user/{userId}")
    public Result<List<AdminCommentVo>> adminGetCommentsByUserId(@PathVariable Integer userId) {
        List<AdminCommentVo> commentList = commentService.adminGetCommentsByUserId(userId);
        return Result.success(commentList);
    }

    /**
     * 管理员搜索评论
     *
     * @param commentSearchDto 搜索条件
     * @return 搜索结果
     */
    @PreAuthorize("hasAuthority('comment:search')")
    @PostMapping("/admin/search")
    public Result<List<AdminCommentVo>> adminSearchComment(@RequestBody CommentSearchDto commentSearchDto) {
        List<AdminCommentVo> commentList = commentService.adminSearchComment(commentSearchDto);
        return Result.success(commentList);
    }

    /**
     * 管理员审核评论
     *
     * @param commentAuditDto 评论审核信息
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('comment:examine')")
    @PutMapping("/admin/examine")
    public Result<Void> adminExamineComment(@RequestBody CommentAuditDto commentAuditDto) {
        commentService.adminExamineComment(commentAuditDto);
        return Result.success();
    }

    /**
     * 管理员批量审核评论
     *
     * @param commentAuditDtos 评论审核信息列表
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('comment:examine')")
    @PutMapping("/admin/examine/batch")
    public Result<Void> adminExamineBatchComment(@RequestBody List<CommentAuditDto> commentAuditDtos) {
        commentService.adminExamineBatchComment(commentAuditDtos);
        return Result.success();
    }

    /**
     * 管理员删除评论
     *
     * @param commentId 评论id
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('comment:delete')")
    @DeleteMapping("/admin/{commentId}")
    public Result<Void> adminDeleteComment(@PathVariable Integer commentId) {
        commentService.adminDeleteComment(commentId);
        return Result.success();
    }

    /**
     * 管理员批量删除评论
     *
     * @param commentIds 评论ID列表
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('comment:delete')")
    @DeleteMapping("/admin/delete/batch")
    public Result<Void> adminDeleteBatchComment(@RequestBody List<Integer> commentIds) {
        commentService.adminDeleteBatchComment(commentIds);
        return Result.success();
    }

    /**
     * 管理员获取评论总数统计
     *
     * @return 评论总数
     */
    @PreAuthorize("hasAuthority('comment:list')")
    @GetMapping("/admin/statistics")
    public Result<Long> getCommentStatistics() {
        Long totalCount = commentService.getCommentTotalCount();
        return Result.success(totalCount);
    }

}
