package com.sidifensen.controller;

import com.sidifensen.domain.result.Result;
import com.sidifensen.service.FollowService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public Result toggleFollow(@PathVariable @NotNull(message = "用户ID不能为空") Integer followedId) {
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
    @GetMapping("/check")
    public Result<Boolean> checkFollowStatus(@RequestParam @NotNull(message = "关注者ID不能为空") Integer followerId,
                                             @RequestParam @NotNull(message = "被关注者ID不能为空") Integer followedId) {
        Boolean isFollowing = followService.isFollowing(followerId, followedId);
        return Result.success(isFollowing);
    }

}
