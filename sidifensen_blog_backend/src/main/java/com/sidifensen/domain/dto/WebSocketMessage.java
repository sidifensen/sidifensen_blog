package com.sidifensen.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * WebSocket 消息 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {

    private String type; // 消息类型：SEND_MESSAGE, READ_MESSAGE, REVOKE_MESSAGE, HEARTBEAT

    private Integer messageId; // 消息ID

    private Integer fromUserId;

    private Integer toUserId;

    private Integer targetUserId;

    private String content;

    private Integer messageType; // 1-文本 2-图片

    private String imageUrl;

    private Date createTime;

    private String fromUserNickname; // 发送者昵称

    private String fromUserAvatar; // 发送者头像

    private String toUserNickname; // 接收者昵称

    private String toUserAvatar; // 接收者头像

    private Integer unreadCount; // 接收者的未读消息数

    private Integer userId; // 用户ID（用于在线状态通知）

    private Boolean isOnline; // 是否在线（用于在线状态通知）

    private Integer code;

    private String message;

    /**
     * 成功消息
     */
    public static WebSocketMessage success(String message) {
        return WebSocketMessage.builder()
                .type("SYSTEM")
                .code(200)
                .message(message)
                .build();
    }

    /**
     * 错误消息
     */
    public static WebSocketMessage error(String message) {
        return WebSocketMessage.builder()
                .type("SYSTEM")
                .code(500)
                .message(message)
                .build();
    }

    /**
     * 心跳消息
     */
    public static WebSocketMessage heartbeat() {
        return WebSocketMessage.builder()
                .type("HEARTBEAT")
                .build();
    }
}
