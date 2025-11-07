package com.sidifensen.controller;

import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.ConversationVo;
import com.sidifensen.service.ConversationService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会话 Controller
 */
@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Resource
    private ConversationService conversationService;

    /**
     * 获取会话列表
     */
    @GetMapping("/list")
    public Result<List<ConversationVo>> getConversationList() {
        Integer userId = SecurityUtils.getUserId();
        List<ConversationVo> result = conversationService.getConversationList(userId);
        return Result.success(result);
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/{targetUserId}")
    public Result<Void> deleteConversation(@PathVariable Integer targetUserId) {
        Integer userId = SecurityUtils.getUserId();
        conversationService.deleteConversation(userId, targetUserId);
        return Result.success();
    }

    /**
     * 清空未读数
     */
    @PutMapping("/read/{targetUserId}")
    public Result<Void> clearUnreadCount(@PathVariable Integer targetUserId) {
        Integer userId = SecurityUtils.getUserId();
        conversationService.clearUnreadCount(userId, targetUserId);
        return Result.success();
    }
}
