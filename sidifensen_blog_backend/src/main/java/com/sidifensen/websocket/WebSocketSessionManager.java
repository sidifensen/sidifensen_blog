package com.sidifensen.websocket;

import com.alibaba.fastjson.JSON;
import com.sidifensen.domain.dto.WebSocketMessage;
import com.sidifensen.domain.enums.WebSocketMessageTypeEnum;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.service.ConversationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
     * 发送消息给指定用户
     */
    public void sendMessage(Integer userId, String message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("发送消息失败，userId: {}", userId, e);
            }
        } else {
            log.warn("用户 {} 不在线，无法发送消息", userId);
        }
    }

    /**
     * 广播消息给所有在线用户
     */
    public void broadcast(String message) {
        sessions.forEach((userId, session) -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error("广播消息失败，userId: {}", userId, e);
                }
            }
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
     * 通知所有与该用户有会话的在线用户
     */
    public void broadcastUserOnlineStatus(Integer userId, boolean isOnline) {
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
                WebSocketSession targetSession = sessions.get(targetUserId);
                if (targetSession != null && targetSession.isOpen()) {
                    try {
                        targetSession.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        log.error("发送在线状态通知失败，targetUserId: {}", targetUserId, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("广播用户在线状态失败，userId: {}, isOnline: {}", userId, isOnline, e);
        }
    }
}
