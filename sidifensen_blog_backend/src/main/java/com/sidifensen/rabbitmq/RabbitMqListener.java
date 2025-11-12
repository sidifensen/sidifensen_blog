package com.sidifensen.rabbitmq;

import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.WebSocketMessageDto;
import com.sidifensen.domain.entity.SysVisitorLog;
import com.sidifensen.domain.enums.MailEnum;
import com.sidifensen.service.SysVisitorLogService;
import com.sidifensen.utils.EmailUtils;
import com.sidifensen.utils.IpUtils;
import com.sidifensen.websocket.WebSocketSessionManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * @author sidifensen
 * @since 2025-07-09
 */
@Component
@Slf4j
public class RabbitMqListener {

    @Resource
    private EmailUtils emailUtils;

    @Resource
    private SysVisitorLogService sysVisitorLogService;

    @Resource
    private IpUtils ipUtils;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private WebSocketSessionManager webSocketSessionManager;

    @Value("${frontend.userHost}")
    private String frontendUserHost;

    @Value("${frontend.adminHost}")
    private String frontendAdminHost;

    /**
     * 监听邮件队列
     * 处理注册、重置密码、重置邮箱验证码
     * <p>
     * 重试机制：
     * - 最大重试次数：5次（包括首次消费）
     * - 重试间隔：5s, 10s, 20s, 30s（指数退避）
     * - 重试失败后：消息进入死信队列
     */
    @RabbitListener(queues = RabbitMQConstants.Email_Queue)
    public void receiveEmail(Map<String, Object> message) {
        String email = null;
        try {
            email = (String) message.get("email");
            String checkCode = (String) message.get("checkCode");
            String type = (String) message.get("type");

            if (type.equals(MailEnum.REGISTER.getType())) {
                emailUtils.sendHtmlMail(email, MailEnum.REGISTER.getSubject(), MailEnum.REGISTER.getTemplateName(),
                        Map.of("checkCode", checkCode, "frontendUserHost", frontendUserHost));
            } else if (type.equals(MailEnum.RESET_PASSWORD.getType())) {
                emailUtils.sendHtmlMail(email, MailEnum.RESET_PASSWORD.getSubject(), MailEnum.RESET_PASSWORD.getTemplateName(),
                        Map.of("checkCode", checkCode, "frontendUserHost", frontendUserHost));
            } else if (type.equals(MailEnum.RESET_EMAIL.getType())) {
                emailUtils.sendHtmlMail(email, MailEnum.RESET_EMAIL.getSubject(), MailEnum.RESET_EMAIL.getTemplateName(),
                        Map.of("checkCode", checkCode, "frontendUserHost", frontendUserHost));
            } else {
                log.warn("未知的邮件类型: {}, 跳过处理", type);
            }

        } catch (Exception e) {
            log.error("处理邮件发送请求时出现异常, email={}, message={}", email, message, e);
            // 抛出异常触发重试机制
            throw new RuntimeException("邮件发送失败: " + e.getMessage(), e);
        }
    }

    /**
     * 监听审核通知队列
     * 发送审核通知邮件给管理员
     * <p>
     * 重试机制：
     * - 最大重试次数：5次（包括首次消费）
     * - 重试间隔：5s, 10s, 20s, 30s（指数退避）
     * - 重试失败后：消息进入死信队列
     */
    @RabbitListener(queues = RabbitMQConstants.Examine_Queue)
    public void receiveExamineEmail(Map<String, Object> message) {
        try {
            String text = (String) message.get("text");

            emailUtils.sendHtmlMailToAdmin(MailEnum.EXAMINE.getSubject(), MailEnum.EXAMINE.getTemplateName(),
                    Map.of("text", text, "frontendAdminHost", frontendAdminHost));

        } catch (Exception e) {
            log.error("处理审核通知邮件发送请求时出现异常, message={}", message, e);
            // 抛出异常触发重试机制
            throw new RuntimeException("审核通知邮件发送失败: " + e.getMessage(), e);
        }
    }

    /**
     * 监听友链审核通过通知队列
     * 发送友链审核通过邮件给用户
     * <p>
     * 重试机制：
     * - 最大重试次数：5次（包括首次消费）
     * - 重试间隔：5s, 10s, 20s, 30s（指数退避）
     * - 重试失败后：消息进入死信队列
     */
    @RabbitListener(queues = RabbitMQConstants.Link_Approval_Queue)
    public void receiveLinkApprovalEmail(Map<String, Object> message) {
        String email = null;
        try {
            email = (String) message.get("email");
            String text = (String) message.get("text");

            emailUtils.sendHtmlMail(email, MailEnum.EXAMINE.getSubject(), MailEnum.EXAMINE.getTemplateName(),
                    Map.of("text", text, "frontendUserHost", frontendUserHost));

        } catch (Exception e) {
            log.error("处理友链审核通过邮件发送请求时出现异常, email={}, message={}", email, message, e);
            // 抛出异常触发重试机制
            throw new RuntimeException("友链审核通过邮件发送失败: " + e.getMessage(), e);
        }
    }

    /**
     * 监听访客记录队列
     * 处理访客记录插入
     */
    @RabbitListener(queues = RabbitMQConstants.Visitor_Queue)
    public void receiveVisitorRecord(Map<String, Object> message) {
        try {
            Integer userId = (Integer) message.get("userId");
            String ip = (String) message.get("ip");
            String device = (String) message.get("device");
            String address = ipUtils.getAddress(ip);

            SysVisitorLog sysVisitorLog = new SysVisitorLog()
                    .setUserId(userId)
                    .setIp(ip)
                    .setAddress(address)
                    .setDevice(device)
                    .setVisitTime(new Date());

            sysVisitorLogService.insertVisitorRecord(sysVisitorLog);

        } catch (Exception e) {
            log.error("处理访客记录时出现异常, message={}", message, e);
            // 不抛出异常，避免重试（访客记录丢失影响不大）
        }
    }

    /**
     * 监听黑名单通知队列
     * 处理黑名单通知邮件发送
     * <p>
     * 重试机制：
     * - 最大重试次数：5次（包括首次消费）
     * - 重试间隔：5s, 10s, 20s, 30s（指数退避）
     * - 重试失败后：消息进入死信队列
     */
    @RabbitListener(queues = RabbitMQConstants.Blacklist_Queue)
    public void receiveBlacklistNotification(Map<String, Object> message) {
        try {
            String userId = String.valueOf(message.get("userId"));
            String ip = (String) message.get("ip");
            String address = (String) message.get("address");
            String reason = (String) message.get("reason");
            String banDuration = (String) message.get("banDuration");
            String banTime = (String) message.get("banTime");

            // 发送邮件给管理员
            emailUtils.sendHtmlMailToAdmin(
                    MailEnum.BLACKLIST_NOTIFICATION.getSubject(),
                    MailEnum.BLACKLIST_NOTIFICATION.getTemplateName(),
                    Map.of(
                            "userId", userId,
                            "ip", ip,
                            "address", address,
                            "reason", reason,
                            "banDuration", banDuration,
                            "banTime", banTime,
                            "frontendAdminHost", frontendAdminHost
                    )
            );

        } catch (Exception e) {
            log.error("处理黑名单通知邮件发送请求时出现异常, message={}", message, e);
            // 抛出异常触发重试机制
            throw new RuntimeException("黑名单通知邮件发送失败: " + e.getMessage(), e);
        }
    }

    /**
     * 监听 WebSocket 消息队列
     * 从 RabbitMQ 消费消息并发送到 WebSocket
     * 
     * 说明：
     * - 用户在线时，消息会直接发送（不经过 RabbitMQ）
     * - 用户不在线或发送失败时，消息会进入 RabbitMQ
     * - 消费者从 RabbitMQ 消费消息，尝试发送给用户
     * 
     * 重试机制：
     * - 如果用户不在线，不抛出异常（避免无意义重试）
     * - 如果发送失败，抛出异常触发重试
     * - 最大重试次数：3次
     * - 重试失败后：消息进入死信队列
     */
    @RabbitListener(
            queues = RabbitMQConstants.WebSocket_Queue,
            concurrency = "1-3"  // 动态并发：最少1个，最多3个消费者线程（根据消息负载自动调整）
    )
    public void consumeWebSocketMessage(@Payload WebSocketMessageDto messageDto) {
        Integer userId = messageDto.getUserId();
        String message = messageDto.getMessage();

        try {
            // 尝试直接发送到 WebSocket
            // 用户不在线时返回 false，静默处理（消息被确认，避免无意义重试）
            // 注意：如果后续需要离线消息推送，可以在这里保存消息到数据库
            webSocketSessionManager.sendToWebSocketDirect(userId, message);
        } catch (RuntimeException e) {
            // 网络错误等异常，抛出异常触发重试
            log.error("发送 WebSocket 消息失败，userId: {}, 将重试", userId, e);
            throw new RuntimeException("发送 WebSocket 消息失败: " + e.getMessage(), e);
        } catch (Exception e) {
            // 未知错误，抛出异常触发重试
            log.error("处理 WebSocket 消息时出现未知错误，userId: {}, 将重试", userId, e);
            throw new RuntimeException("处理 WebSocket 消息失败: " + e.getMessage(), e);
        }
    }

}
