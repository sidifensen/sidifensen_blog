package com.sidifensen.controller;

import com.sidifensen.domain.dto.CommentDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.CommentVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.service.ICommentService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 *
 * @author sidifensen
 * @since 2025-09-15
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private ICommentService commentService;

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
    public Result<PageVo<List<CommentVo>>> getCommentList(@NotNull Integer articleId,
                                                          @NotNull Integer pageNum,
                                                          @NotNull Integer pageSize) {
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
    public Result<PageVo<List<CommentVo>>> getReplyList(@NotNull Integer commentId,
                                                        @NotNull Integer pageNum,
                                                        @NotNull Integer pageSize) {
        PageVo<List<CommentVo>> replyList = commentService.getReplyList(commentId, pageNum, pageSize);
        return Result.success(replyList);
    }

    /**
     * 管理员审核评论
     *
     * @param commentId     评论id
     * @param examineStatus 审核状态 0-待审核 1-审核通过 2-审核未通过
     * @return 操作结果
     */
    @PreAuthorize("hasAuthority('comment:audit')")
    @PutMapping("/admin/audit/{commentId}")
    public Result<Void> auditComment(@PathVariable Integer commentId,
                                     @NotNull Integer examineStatus) {
        commentService.auditComment(commentId, examineStatus);
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
}
