package com.sidifensen.controller;


import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.MessageVo;
import com.sidifensen.service.MessageService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-08-17
 */
@RateLimit
@Validated
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    /**
     * 获取管理员未读消息数量
     *
     * @param
     * @return
     */
    @PreAuthorize("hasAuthority('message:count')")
    @GetMapping("/admin/count")
    public Result getAdminMessagesCount() {
        Integer count = messageService.getAdminMessagesCount();
        return Result.success(count);
    }

    /**
     * 查看管理员消息
     *
     * @param
     * @return
     */
    @PreAuthorize("hasAuthority('message:list')")
    @GetMapping("/admin/list")
    public Result getAdminMessages() {
        List<MessageVo> messageVos = messageService.getAdminMessages();
        return Result.success(messageVos);
    }

    /**
     * 管理员读取消息/批量读取消息
     *
     * @param messageIds
     * @return
     */
    @PreAuthorize("hasAuthority('message:read')")
    @PutMapping("/admin/read")
    public Result readAdminMessage(@RequestBody @NotNull(message = "消息ID列表不能为空") List<Integer> messageIds) {
        messageService.readAdminMessages(messageIds);
        return Result.success();
    }

    /**
     * 管理员删除消息/批量删除消息
     *
     * @param messageIds
     * @return
     */
    @PreAuthorize("hasAuthority('message:delete')")
    @DeleteMapping("/admin/delete")
    public Result deleteAdminMessage(@RequestBody @NotNull(message = "消息ID列表不能为空") List<Integer> messageIds) {
        messageService.deleteAdminMessages(messageIds);
        return Result.success();
    }


}
