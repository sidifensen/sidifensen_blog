package com.sidifensen.websocket;

import com.alibaba.fastjson.JSON;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.WebSocketMessage;
import com.sidifensen.domain.entity.PrivateMessage;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.WebSocketMessageTypeEnum;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.ConversationService;
import com.sidifensen.service.PrivateMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * WebSocket 消息处理器
 * 处理私信相关的 WebSocket 消息
 */
@Component
@Slf4j
public class PrivateMessageWebSocketHandler extends TextWebSocketHandler {

    @Resource
    private WebSocketSessionManager sessionManager;

    @Resource
    private PrivateMessageService privateMessageService;

    @Resource
    private ConversationService conversationService;

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * WebSocket 连接建立后的处理
     * 1. 注册用户会话到会话管理器
     * 2. 广播用户上线状态给所有相关用户
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Integer userId = (Integer) session.getAttributes().get("userId");
        sessionManager.registerSession(userId, session);

        // 广播用户上线状态
        sessionManager.broadcastUserOnlineStatus(userId, true);
    }

    /**
     * 处理接收到的 WebSocket 文本消息
     * 根据消息类型分发到不同的处理方法：
     * - SEND_MESSAGE: 发送私信
     * - READ_MESSAGE: 标记消息已读
     * - REVOKE_MESSAGE: 撤回消息
     * - HEARTBEAT: 心跳保活
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            Integer fromUserId = (Integer) session.getAttributes().get("userId");
            String payload = message.getPayload();

            // 解析消息 {"type": "SEND_MESSAGE","content": "你好","toUserId": 123}
            WebSocketMessage wsMessage = JSON.parseObject(payload, WebSocketMessage.class);
            String type = wsMessage.getType();

            if (WebSocketMessageTypeEnum.SEND_MESSAGE.getType().equals(type)) {
                // 发送消息
                handleSendMessage(fromUserId, wsMessage);
            } else if (WebSocketMessageTypeEnum.READ_MESSAGE.getType().equals(type)) {
                // 标记消息已读
                handleReadMessage(fromUserId, wsMessage);
            } else if (WebSocketMessageTypeEnum.REVOKE_MESSAGE.getType().equals(type)) {
                // 撤回消息
                handleRevokeMessage(fromUserId, wsMessage);
            } else if (WebSocketMessageTypeEnum.HEARTBEAT.getType().equals(type)) {
                // 心跳保活
                handleHeartbeat(session);
            } else {
                log.warn("未知的消息类型: {}", type);
            }
        } catch (Exception e) {
            log.error("处理 WebSocket 消息异常", e);
        }
    }

    /**
     * WebSocket 连接关闭后的处理
     * 1. 广播用户下线状态给所有相关用户（在移除会话前广播）
     * 2. 从会话管理器中移除用户会话
     * 3. 清除 Redis 中的在线状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Integer userId = (Integer) session.getAttributes().get("userId");

        // 广播用户下线状态（在移除会话之前广播，确保能正常发送）
        sessionManager.broadcastUserOnlineStatus(userId, false);

        // 移除用户会话
        sessionManager.removeSession(userId);
    }

    /**
     * 处理 WebSocket 传输异常
     * 记录异常日志，便于排查问题
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        Integer userId = (Integer) session.getAttributes().get("userId");
        log.error("WebSocket 传输异常，userId: {}", userId, exception);
    }

    /**
     * 处理发送消息
     */
    private void handleSendMessage(Integer fromUserId, WebSocketMessage wsMessage) {
        Integer toUserId = wsMessage.getToUserId();
        String content = wsMessage.getContent();
        Integer messageType = wsMessage.getMessageType();
        String imageUrl = wsMessage.getImageUrl();

        // 1. 保存消息到数据库
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setFromUserId(fromUserId);
        privateMessage.setToUserId(toUserId);
        privateMessage.setContent(content);
        privateMessage.setMessageType(messageType);
        privateMessage.setImageUrl(imageUrl);
        privateMessageService.save(privateMessage);

        // 2. 更新双方的会话记录
        conversationService.updateConversation(fromUserId, toUserId, privateMessage);

        // 3. 查询用户信息
        SysUser fromUser = sysUserMapper.selectById(fromUserId);
        SysUser toUser = sysUserMapper.selectById(toUserId);

        // 4. 推送消息给接收者（如果在线）
        WebSocketSession toSession = sessionManager.getSession(toUserId);
        if (toSession != null && toSession.isOpen()) {
            // 查询接收者与发送者的会话未读数
            Integer unreadCount = conversationService.getUnreadCount(toUserId, fromUserId);

            WebSocketMessage pushMessage = WebSocketMessage.builder()
                    .type(WebSocketMessageTypeEnum.NEW_MESSAGE.getType())
                    .messageId(privateMessage.getId())
                    .fromUserId(fromUserId)
                    .toUserId(toUserId)
                    .content(content)
                    .messageType(messageType)
                    .imageUrl(imageUrl)
                    .createTime(privateMessage.getCreateTime())
                    .unreadCount(unreadCount)
                    .build();

            // 添加用户信息
            if (fromUser != null) {
                pushMessage.setFromUserNickname(fromUser.getNickname());
                pushMessage.setFromUserAvatar(fromUser.getAvatar());
            }
            if (toUser != null) {
                pushMessage.setToUserNickname(toUser.getNickname());
                pushMessage.setToUserAvatar(toUser.getAvatar());
            }

            sendMessage(toSession, pushMessage);
        }
        // 如果接收者不在线，消息已保存到数据库，上线后会通过 HTTP API 获取

        // 5. 发送成功回执给发送者
        WebSocketSession fromSession = sessionManager.getSession(fromUserId);
        if (fromSession != null && fromSession.isOpen()) {
            WebSocketMessage ackMessage = WebSocketMessage.builder()
                    .type(WebSocketMessageTypeEnum.SEND_SUCCESS.getType())
                    .messageId(privateMessage.getId())
                    .build();
            sendMessage(fromSession, ackMessage);
        }
    }

    /**
     * 处理已读消息
     */
    private void handleReadMessage(Integer userId, WebSocketMessage wsMessage) {
        Integer targetUserId = wsMessage.getTargetUserId();

        // 批量更新消息为已读
        privateMessageService.markAsRead(targetUserId, userId);

        // 更新会话未读数
        conversationService.clearUnreadCount(userId, targetUserId);

        // 通知发送者消息已读
        WebSocketSession targetSession = sessionManager.getSession(targetUserId);
        if (targetSession != null && targetSession.isOpen()) {
            WebSocketMessage readNotify = WebSocketMessage.builder()
                    .type(WebSocketMessageTypeEnum.MESSAGE_READ.getType())
                    .fromUserId(userId)
                    .build();
            sendMessage(targetSession, readNotify);
        }
    }

    /**
     * 处理撤回消息
     */
    private void handleRevokeMessage(Integer userId, WebSocketMessage wsMessage) {
        Integer messageId = wsMessage.getMessageId();
        WebSocketSession fromSession = sessionManager.getSession(userId);

        try {
            // 撤回消息
            PrivateMessage message = privateMessageService.revokeMessage(messageId, userId);

            if (message != null) {
                Integer toUserId = message.getToUserId();

                // 更新双方的会话，将最后一条消息内容设置为 "撤回了一条消息"
                conversationService.updateLastMessageAfterRevoke(userId, toUserId, BlogConstants.RevokedMessage);

                // 通知发送者撤回成功
                if (fromSession != null && fromSession.isOpen()) {
                    WebSocketMessage successMessage = WebSocketMessage.builder()
                            .type(WebSocketMessageTypeEnum.REVOKE_SUCCESS.getType())
                            .messageId(messageId)
                            .build();
                    sendMessage(fromSession, successMessage);
                }

                // 通知接收者消息被撤回
                WebSocketSession toSession = sessionManager.getSession(toUserId);
                if (toSession != null && toSession.isOpen()) {
                    WebSocketMessage revokeNotify = WebSocketMessage.builder()
                            .type(WebSocketMessageTypeEnum.MESSAGE_REVOKED.getType())
                            .messageId(messageId)
                            .fromUserId(userId)
                            .toUserId(toUserId)
                            .content(BlogConstants.RevokedMessage) // 撤回提示文本
                            .build();
                    sendMessage(toSession, revokeNotify);
                }
            }
        } catch (Exception e) {
            log.error("撤回消息失败: messageId={}, userId={}", messageId, userId, e);
            // 通知发送者撤回失败
            if (fromSession != null && fromSession.isOpen()) {
                WebSocketMessage errorMessage = WebSocketMessage.builder()
                        .type(WebSocketMessageTypeEnum.REVOKE_FAILED.getType())
                        .messageId(messageId)
                        .message(e.getMessage())
                        .build();
                sendMessage(fromSession, errorMessage);
            }
        }
    }

    /**
     * 处理心跳
     */
    private void handleHeartbeat(WebSocketSession session) {
        sendMessage(session, WebSocketMessage.heartbeat());
    }

    /**
     * 发送消息
     */
    private void sendMessage(WebSocketSession session, Object message) {
        try {
            String json = JSON.toJSONString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.error("发送 WebSocket 消息失败", e);
        }
    }
}
