package com.sidifensen.websocket;

import com.alibaba.fastjson.JSON;
import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.WebSocketMessage;
import com.sidifensen.domain.dto.WebSocketMessageDto;
import com.sidifensen.domain.enums.WebSocketMessageTypeEnum;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.service.ConversationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * WebSocket 会话管理器
 * 管理用户的 WebSocket 连接，提供在线状态管理和消息发送功能
 */
@Component
@Slf4j
public class WebSocketSessionManager {

    // 存储 userId 和 WebSocketSession 的映射关系
    private final ConcurrentHashMap<Integer, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private ConversationService conversationService;

    // WebSocket 消息发送线程池
    @Resource(name = "webSocketExecutor")
    private Executor webSocketExecutor;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 注册会话
     */
    public void registerSession(Integer userId, WebSocketSession session) {
        sessions.put(userId, session);
        // 更新 Redis 在线状态（30分钟）
        redisComponent.setUserOnlineStatus(userId);
    }

    /**
     * 移除会话
     */
    public void removeSession(Integer userId) {
        sessions.remove(userId);
        // 删除 Redis 在线状态
        redisComponent.removeUserOnlineStatus(userId);
    }

    /**
     * 获取会话
     */
    public WebSocketSession getSession(Integer userId) {
        return sessions.get(userId);
    }

    /**
     * 检查用户是否在线
     */
    public boolean isOnline(Integer userId) {
        return sessions.containsKey(userId);
    }

    /**
     * 发送消息给指定用户（混合模式：在线用户直接发送，离线用户使用 RabbitMQ）
     * 
     * 策略：
     * 1. 用户在线：直接发送到 WebSocket（最快，延迟 1-10ms）
     * 2. 用户不在线或发送失败：使用 RabbitMQ（保证可靠性）
     * 
     * @param userId  用户ID
     * @param message 消息内容（JSON字符串）
     */
    public void sendMessage(Integer userId, String message) {
        WebSocketSession session = sessions.get(userId);

        // 方案1：用户在线，直接发送（最快）
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                return;  // 成功，直接返回
            } catch (IOException e) {
                log.warn("直接发送失败，userId: {}, 降级到 RabbitMQ", userId, e);
                // 发送失败，降级到 RabbitMQ
            }
        }

        // 方案2：用户不在线或发送失败，使用 RabbitMQ（保证可靠性）
        sendMessageViaRabbitMQ(userId, message);
    }

    /**
     * 通过 RabbitMQ 发送消息（用于离线消息或发送失败重试）
     * 
     * @param userId  用户ID
     * @param message 消息内容（JSON字符串）
     */
    private void sendMessageViaRabbitMQ(Integer userId, String message) {
        try {
            // 构造消息 DTO
            WebSocketMessageDto messageDto = WebSocketMessageDto.builder()
                    .userId(userId)
                    .message(message)
                    .build();

            // 构造路由键
            String routingKey = RabbitMQConstants.WebSocket_Routing_Key_Prefix + userId;

            // 发送到 RabbitMQ（使用持久化消息，确保服务器重启后消息不丢失）
            rabbitTemplate.convertAndSend(
                    RabbitMQConstants.WebSocket_Exchange,
                    routingKey,
                    messageDto,
                    msg -> {
                        // 持久化消息，确保可靠性
                        msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return msg;
                    }
            );

            log.debug("消息已发送到 RabbitMQ，userId: {}", userId);
        } catch (Exception e) {
            log.error("发送消息到 RabbitMQ 失败，userId: {}", userId, e);
        }
    }

    /**
     * 直接发送消息到 WebSocket（由 RabbitMQ 消费者调用）
     * 不经过队列，直接发送
     * 
     * @param userId  用户ID
     * @param message 消息内容（JSON字符串）
     * @return true 发送成功，false 用户不在线（静默处理，不抛异常）
     * @throws RuntimeException 如果发送过程中出现网络错误等异常（需要重试）
     */
    public boolean sendToWebSocketDirect(Integer userId, String message) {
        WebSocketSession session = sessions.get(userId);
        if (session == null || !session.isOpen()) {
            // 用户不在线，返回 false（不抛异常，避免无意义重试）
            return false;
        }

        try {
            session.sendMessage(new TextMessage(message));
            return true;
        } catch (IOException e) {
            // 网络错误等异常，抛出异常触发重试
            log.error("发送 WebSocket 消息失败，userId: {}", userId, e);
            throw new RuntimeException("发送消息失败", e);
        }
    }

    /**
     * 广播消息给所有在线用户（异步）
     */
    public void broadcast(String message) {
        // 异步执行广播任务
        webSocketExecutor.execute(() -> {
            sessions.forEach((userId, session) -> {
                if (session != null && session.isOpen()) {
                    sendMessage(userId, message);
                }
            });
        });
    }

    /**
     * 获取在线用户数量
     */
    public int getOnlineCount() {
        return sessions.size();
    }

    /**
     * 广播用户在线状态变化
     * 通知所有与该用户有会话的在线用户（异步）
     */
    public void broadcastUserOnlineStatus(Integer userId, boolean isOnline) {
        // 异步执行广播任务
        webSocketExecutor.execute(() -> {
            try {
                // 获取与该用户有会话的所有用户ID
                List<Integer> conversationUserIds = conversationService.getConversationUserIds(userId);

                // 构造在线状态消息
                WebSocketMessage statusMessage = WebSocketMessage.builder()
                        .type(WebSocketMessageTypeEnum.USER_ONLINE_STATUS.getType())
                        .userId(userId)
                        .isOnline(isOnline)
                        .build();

                String message = JSON.toJSONString(statusMessage);

                // 向每个有会话的在线用户发送状态更新
                for (Integer targetUserId : conversationUserIds) {
                    sendMessage(targetUserId, message);
                }
            } catch (Exception e) {
                log.error("广播用户在线状态失败，userId: {}, isOnline: {}", userId, isOnline, e);
            }
        });
    }
}
