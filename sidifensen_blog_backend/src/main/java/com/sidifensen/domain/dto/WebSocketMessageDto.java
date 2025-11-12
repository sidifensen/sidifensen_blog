package com.sidifensen.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * WebSocket 消息 DTO（用于 RabbitMQ）
 * 用于在 RabbitMQ 中传输 WebSocket 消息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 目标用户ID
     */
    private Integer userId;

    /**
     * 消息内容（JSON字符串）
     */
    private String message;

    /**
     * 消息类型（可选，用于区分消息类型）
     */
    private String messageType;
}

