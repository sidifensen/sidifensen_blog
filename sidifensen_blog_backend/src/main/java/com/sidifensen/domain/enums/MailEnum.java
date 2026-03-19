package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailEnum {

    REGISTER("register", "欢迎注册 sidifensen_blog", "register", "用户注册"),
    RESET_PASSWORD("resetPassword","sidifensen_blog 重置密码", "reset-password","重置密码"),
    RESET_EMAIL("resetEmail","sidifensen_blog 重置邮箱", "reset-email","重置邮箱"),
    EXAMINE("examine","sidifensen_blog 审核通知", "examine","审核通知"),
    BLACKLIST_NOTIFICATION("blacklistNotification","sidifensen_blog 黑名单通知", "blacklist-notification","黑名单通知"),
    COMMENT_NOTIFICATION("commentNotification","sidifensen_blog 评论通知", "comment-notification","评论通知"),
    SYSTEM_NOTIFICATION("systemNotification","sidifensen_blog 系统通知", "system-notification","系统通知");

    // 邮件类型
    private final String type;
    // 邮箱主题
    private final String subject;
    // 模板名称
    private final String templateName;
    // 描述
    private final String desc;
}
