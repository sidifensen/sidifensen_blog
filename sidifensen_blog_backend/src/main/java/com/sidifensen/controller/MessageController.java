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
     * 查看管理员消息数量
     * @param
     * @return
     */
    @PreAuthorize("hasAuthority('message:count')")
    @GetMapping("/admin/count")
    public Result<Object> getMessageCount() {
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
    public Result<Object> getMessages() {
        List<MessageVo> messageVos = messageService.getMessages();
        return Result.success(messageVos);
    }

}
