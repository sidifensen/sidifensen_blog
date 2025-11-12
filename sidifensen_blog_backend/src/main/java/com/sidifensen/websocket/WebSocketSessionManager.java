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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * WebSocket 会话管理器
 * 管理用户的 WebSocket 连接，提供在线状态管理和消息发送功能
 */
@Component
@Slf4j
public class WebSocketSessionManager {

    // 存储 userId 和 WebSocketSession 的映射关系
    private final ConcurrentHashMap<Integer, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 为每个用户维护一个消息队列，保证消息顺序
    // Key: userId, Value: 该用户的消息队列
    private final ConcurrentHashMap<Integer, ConcurrentLinkedQueue<String>> userMessageQueues = new ConcurrentHashMap<>();

    // 标记每个用户是否正在处理消息队列
    // Key: userId, Value: 是否正在处理
    private final ConcurrentHashMap<Integer, AtomicBoolean> processingFlags = new ConcurrentHashMap<>();

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private ConversationService conversationService;

    // WebSocket 消息发送线程池
    @Resource(name = "webSocketExecutor")
    private Executor webSocketExecutor;

    /**
     * 注册会话
     */
    public void registerSession(Integer userId, WebSocketSession session) {
        sessions.put(userId, session);
        // 初始化用户消息队列和处理标志
        userMessageQueues.putIfAbsent(userId, new ConcurrentLinkedQueue<>());
        processingFlags.putIfAbsent(userId, new AtomicBoolean(false));
        // 更新 Redis 在线状态（30分钟）
        redisComponent.setUserOnlineStatus(userId);
    }

    /**
     * 移除会话
     */
    public void removeSession(Integer userId) {
        sessions.remove(userId);
        // 清理用户消息队列和处理标志
        userMessageQueues.remove(userId);
        processingFlags.remove(userId);
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
     * 发送消息给指定用户（异步，保证顺序）
     * 使用消息队列保证同一用户的消息按顺序发送
     * 
     * @param userId  用户ID
     * @param message 消息内容（JSON字符串）
     */
    public void sendMessage(Integer userId, String message) {
        WebSocketSession session = sessions.get(userId);
        if (session == null || !session.isOpen()) {
            log.warn("用户 {} 不在线，无法发送消息", userId);
            return;
        }

        // 将消息加入队列
        ConcurrentLinkedQueue<String> queue = userMessageQueues.get(userId);
        if (queue == null) {
            // 如果队列不存在，说明用户已断开连接，直接返回
            return;
        }
        queue.offer(message);

        // 尝试处理队列（如果当前没有在处理）
        AtomicBoolean processing = processingFlags.get(userId);
        if (processing != null && processing.compareAndSet(false, true)) {
            // 异步处理消息队列
            webSocketExecutor.execute(() -> {
                try {
                    processMessageQueue(userId, session);
                } finally {
                    processing.set(false);
                }
            });
        }
    }

    /**
     * 处理用户消息队列（保证顺序发送）
     * 持续处理队列直到为空，即使处理过程中有新消息加入也会继续处理
     * 
     * @param userId  用户ID
     * @param session WebSocket会话
     */
    private void processMessageQueue(Integer userId, WebSocketSession session) {
        ConcurrentLinkedQueue<String> queue = userMessageQueues.get(userId);
        if (queue == null) {
            return;
        }

        // 持续处理队列中的所有消息，直到队列为空
        // 设置最大处理次数，防止无限循环（例如恶意用户不断发送消息）
        // 每次处理一批消息后，如果队列中还有消息，会再次尝试获取处理权
        int maxProcessCount = 1000;
        int processedCount = 0;

        while (processedCount < maxProcessCount) {
            // 再次检查会话是否有效
            if (session == null || !session.isOpen()) {
                log.warn("用户 {} 的 WebSocket 会话已关闭，清空消息队列", userId);
                queue.clear();
                return;
            }

            // 从队列头部取出消息
            String message = queue.poll();
            if (message == null) {
                // 队列为空，退出循环
                break;
            }

            // 发送消息
            try {
                session.sendMessage(new TextMessage(message));
                processedCount++;
            } catch (IOException e) {
                log.error("发送消息失败，userId: {}, 清空剩余消息队列", userId, e);
                queue.clear();
                return;
            }
        }

        // 如果达到最大处理次数且队列中还有消息，记录警告日志
        // 注意：此时会释放处理权（在 finally 块中），如果队列中还有消息，
        // 下次 sendMessage 调用时会再次触发处理
        if (processedCount >= maxProcessCount && !queue.isEmpty()) {
            log.warn("用户 {} 的消息队列处理达到最大次数限制（{} 条），剩余 {} 条消息待处理，将稍后继续处理", 
                    userId, maxProcessCount, queue.size());
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
