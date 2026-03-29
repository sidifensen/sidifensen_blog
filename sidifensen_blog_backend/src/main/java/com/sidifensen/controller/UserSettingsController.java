package com.sidifensen.controller;

import com.sidifensen.domain.entity.UserSettings;
import com.sidifensen.domain.result.Result;
import com.sidifensen.service.UserSettingsService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户个人设置控制器
 * </p>
 *
 * @author sidifensen
 * @since 2026-03-18
 */
@Slf4j
@RestController
@RequestMapping("/user/settings")
public class UserSettingsController {

    @Resource
    private UserSettingsService userSettingsService;

    /**
     * 获取用户的所有设置
     *
     * @return 设置对象
     */
    @GetMapping
    public Result getSettings() {
        Integer userId = SecurityUtils.getUserId();
        UserSettings settings = userSettingsService.getSettings(userId);
        return Result.success(settings);
    }

    /**
     * 更新私信邮件通知设置
     *
     * @param isReceive 是否接收（0-关闭，1-开启）
     * @return 操作结果
     */
    @PutMapping("/private_message_email")
    public Result updatePrivateMessageEmailSetting(@RequestParam Integer isReceive) {
        Integer userId = SecurityUtils.getUserId();
        userSettingsService.setReceivePrivateMessageEmail(userId, isReceive);
        return Result.success();
    }

    /**
     * 更新评论邮件通知设置
     *
     * @param isReceive 是否接收（0-关闭，1-开启）
     * @return 操作结果
     */
    @PutMapping("/comment_email")
    public Result updateCommentEmailSetting(@RequestParam Integer isReceive) {
        Integer userId = SecurityUtils.getUserId();
        userSettingsService.setReceiveCommentEmail(userId, isReceive);
        return Result.success();
    }

    /**
     * 更新系统邮件通知设置
     *
     * @param isReceive 是否接收（0-关闭，1-开启）
     * @return 操作结果
     */
    @PutMapping("/system_email")
    public Result updateSystemEmailSetting(@RequestParam Integer isReceive) {
        Integer userId = SecurityUtils.getUserId();
        userSettingsService.setReceiveSystemEmail(userId, isReceive);
        return Result.success();
    }

}
