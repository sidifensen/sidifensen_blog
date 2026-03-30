package com.sidifensen.controller;

import com.sidifensen.aspect.OperationLog;
import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.MessageVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.enums.OperationTypeEnum;
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
     * 查看管理员消息（仅 type=0 系统通知，用于 Bell 下拉）
     *
     * @return
     */
    @OperationLog(module = "消息管理", type = OperationTypeEnum.SELECT, description = "管理员获取消息列表")
    @PreAuthorize("hasAuthority('message:list')")
    @GetMapping("/admin/list")
    public Result getAdminMessages() {
        List<MessageVo> messageVos = messageService.getAdminMessages();
        return Result.success(messageVos);
    }

    /**
     * 分页查看管理员消息（仅 type=0 系统通知，用于管理页面）
     *
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return
     */
    @OperationLog(module = "消息管理", type = OperationTypeEnum.SELECT, description = "管理员分页获取消息列表")
    @PreAuthorize("hasAuthority('message:list')")
    @GetMapping("/admin/page")
    public Result getAdminMessagesPage(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        PageVo<List<MessageVo>> pageVo = messageService.getAdminMessagesPage(pageNum, pageSize);
        return Result.success(pageVo);
    }

    /**
     * 管理员读取消息/批量读取消息
     *
     * @param messageIds
     * @return
     */
    @OperationLog(module = "消息管理", operation = "读取", type = OperationTypeEnum.OTHER, description = "管理员读取消息")
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
    @OperationLog(module = "消息管理", type = OperationTypeEnum.DELETE, description = "管理员删除消息")
    @PreAuthorize("hasAuthority('message:delete')")
    @DeleteMapping("/admin/delete")
    public Result deleteAdminMessage(@RequestBody @NotNull(message = "消息ID列表不能为空") List<Integer> messageIds) {
        messageService.deleteAdminMessages(messageIds);
        return Result.success();
    }

    /**
     * 获取当前用户的系统通知（按类型）
     *
     * @param type     消息类型：0-系统 1-评论 2-点赞 3-收藏 4-关注
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return
     */
    @GetMapping("/notifications")
    public Result getUserNotifications(
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(messageService.getUserNotifications(type, pageNum, pageSize));
    }

    /**
     * 获取当前用户未读通知数量（按类型统计）
     *
     * @return
     */
    @GetMapping("/notifications/unread/count")
    public Result getUnreadNotificationCount() {
        return Result.success(messageService.getUnreadNotificationCount());
    }

    /**
     * 标记通知为已读
     *
     * @param messageIds 消息ID列表
     * @return
     */
    @PutMapping("/notifications/read")
    public Result markNotificationsAsRead(@RequestBody @NotNull(message = "消息ID列表不能为空") List<Integer> messageIds) {
        messageService.markNotificationsAsRead(messageIds);
        return Result.success();
    }

    /**
     * 删除通知
     *
     * @param messageIds 消息ID列表
     * @return
     */
    @DeleteMapping("/notifications")
    public Result deleteNotifications(@RequestBody @NotNull(message = "消息ID列表不能为空") List<Integer> messageIds) {
        messageService.deleteNotifications(messageIds);
        return Result.success();
    }

}
