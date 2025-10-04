package com.sidifensen.controller;

import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.result.Result;
import com.sidifensen.service.LikeService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sidifensen
 * @since 2025-09-15
 */
@RateLimit(30)
@Validated
@RestController
@RequestMapping("/like")
public class LikeController {

    @Resource
    private LikeService likeService;

    /**
     * 点赞或取消点赞
     *
     * @param type   点赞类型 0-文章 1-评论
     * @param typeId 点赞类型id
     * @return 点赞结果
     */
    @PostMapping("/toggle")
    public Result<Void> toggleLike(@NotNull Integer type, @NotNull Integer typeId) {
        likeService.toggleLike(type, typeId);
        return Result.success();
    }

    /**
     * 判断当前用户是否已点赞
     *
     * @param type   点赞类型 0-文章 1-评论
     * @param typeId 点赞类型id
     * @return 是否已点赞
     */
    @GetMapping("/isLiked")
    public Result<Boolean> isLiked(@NotNull Integer type, @NotNull Integer typeId) {
        Boolean isLiked = likeService.isLiked(type, typeId);
        return Result.success(isLiked);
    }

}
