package com.sidifensen.controller;

import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.SysUserVo;
import com.sidifensen.service.FollowService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-09-25
 */
@RestController
@RequestMapping("/follow")
@Slf4j
@Validated
public class FollowController {

    @Resource
    private FollowService followService;

    /**
     * 切换关注状态
     * 如果已关注则取消关注，如果未关注则进行关注
     *
     * @param followedId 被关注用户ID
     */
    @PostMapping("/toggle/{followedId}")
    public Result<Void> toggleFollow(@PathVariable @NotNull(message = "用户ID不能为空") Integer followedId) {
        followService.toggleFollow(followedId);
        return Result.success();
    }

    /**
     * 检查是否已关注某用户
     *
     * @param followerId 关注者ID
     * @param followedId 被关注者ID
     * @return 是否已关注
     */
    @GetMapping("/isFollowing")
    public Result<Boolean> isFollowing(@RequestParam(defaultValue = "0") @NotNull(message = "关注者ID不能为空") Integer followerId,
                                       @RequestParam(defaultValue = "0") @NotNull(message = "被关注者ID不能为空") Integer followedId) {
        Boolean isFollowing = followService.isFollowing(followerId, followedId);
        return Result.success(isFollowing);
    }

    /**
     * 获取用户的关注列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 关注用户列表
     */
    @GetMapping("/followList/{userId}")
    public Result<PageVo<List<SysUserVo>>> getFollowList(
            @PathVariable @NotNull(message = "用户ID不能为空") Integer userId,
            @RequestParam(defaultValue = "1") @NotNull(message = "页码不能为空") Integer pageNum,
            @RequestParam(defaultValue = "10") @NotNull(message = "每页大小不能为空") Integer pageSize) {

        PageVo<List<SysUserVo>> result = followService.getFollowList(userId, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 获取用户的粉丝列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 粉丝用户列表
     */
    @GetMapping("/fansList/{userId}")
    public Result<PageVo<List<SysUserVo>>> getFansList(
            @PathVariable @NotNull(message = "用户ID不能为空") Integer userId,
            @RequestParam(defaultValue = "1") @NotNull(message = "页码不能为空") Integer pageNum,
            @RequestParam(defaultValue = "10") @NotNull(message = "每页大小不能为空") Integer pageSize) {

        PageVo<List<SysUserVo>> result = followService.getFansList(userId, pageNum, pageSize);
        return Result.success(result);
    }

}
