package com.sidifensen.controller;


import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.MessageVo;
import com.sidifensen.service.IMessageService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-08-17
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private IMessageService messageService;

    /**
     * 获取管理员未读消息数量
     * @param
     * @return
     */
    @PreAuthorize("hasAuthority('message:count')")
    @GetMapping("/admin/count")
    public Result getMessageCount() {
        Integer count = messageService.getMessageCount();
        return Result.success(count);
    }

    /**
     * 查看管理员消息
     * @param
     * @return
     */
    @PreAuthorize("hasAuthority('message:list')")
    @GetMapping("/admin/list")
    public Result getMessages() {
        List<MessageVo> messageVos = messageService.getMessages();
        return Result.success(messageVos);
    }

    /**
     * 管理员读取消息
     * @param messageId
     * @return
     */
    @PreAuthorize("hasAuthority('message:read')")
    @GetMapping("/admin/read")
    public Result readMessage(Integer messageId) {
        messageService.readMessage(messageId);
        return Result.success();
    }

}
