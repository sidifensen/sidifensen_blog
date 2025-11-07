package com.sidifensen.domain.enums;

import lombok.Getter;

/**
 * WebSocket 消息类型枚举
 */
@Getter
public enum WebSocketMessageTypeEnum {
    SEND_MESSAGE("SEND_MESSAGE", "发送消息"),
    NEW_MESSAGE("NEW_MESSAGE", "新消息通知"),
    READ_MESSAGE("READ_MESSAGE", "标记已读"),
    MESSAGE_READ("MESSAGE_READ", "消息已读通知"),
    REVOKE_MESSAGE("REVOKE_MESSAGE", "撤回消息"),
    REVOKE_SUCCESS("REVOKE_SUCCESS", "撤回成功"),
    REVOKE_FAILED("REVOKE_FAILED", "撤回失败"),
    MESSAGE_REVOKED("MESSAGE_REVOKED", "消息撤回通知"),
    SEND_SUCCESS("SEND_SUCCESS", "发送成功"),
    HEARTBEAT("HEARTBEAT", "心跳"),
    USER_ONLINE_STATUS("USER_ONLINE_STATUS", "用户在线状态"),
    SYSTEM("SYSTEM", "系统消息");

    private final String type;
    private final String desc;

    WebSocketMessageTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
