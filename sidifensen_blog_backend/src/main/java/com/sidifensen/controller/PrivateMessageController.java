package com.sidifensen.controller;

import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.PrivateMessageVo;
import com.sidifensen.service.PrivateMessageService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 私信 Controller
 */
@RestController
@RequestMapping("/message")
public class PrivateMessageController {

    @Resource
    private PrivateMessageService privateMessageService;

    /**
     * 获取聊天记录
     */
    @GetMapping("/history")
    public Result<PageVo<List<PrivateMessageVo>>> getChatHistory(@NotNull Integer targetUserId,
            @NotNull Integer pageNum,
            @NotNull Integer pageSize) {
        Integer userId = SecurityUtils.getUserId();
        PageVo<List<PrivateMessageVo>> result = privateMessageService.getChatHistory(
                userId, targetUserId, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 撤回消息
     */
    @PutMapping("/revoke/{messageId}")
    public Result<Void> revokeMessage(@PathVariable Integer messageId) {
        Integer userId = SecurityUtils.getUserId();
        privateMessageService.revokeMessage(messageId, userId);
        return Result.success();
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public Result<Void> deleteMessage(@PathVariable Integer messageId) {
        Integer userId = SecurityUtils.getUserId();
        privateMessageService.deleteMessage(messageId, userId);
        return Result.success();
    }

    /**
     * 获取未读消息数
     */
    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount() {
        Integer userId = SecurityUtils.getUserId();
        Integer count = privateMessageService.getUnreadCount(userId);
        return Result.success(count);
    }
}
