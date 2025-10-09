package com.sidifensen.rabbitmq;

import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.constants.RedisConstants;
import com.sidifensen.domain.entity.SysVisitorLog;
import com.sidifensen.domain.enums.MailEnum;
import com.sidifensen.service.SysVisitorLogService;
import com.sidifensen.utils.EmailUtils;
import com.sidifensen.utils.IpUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

            log.info("开始处理邮件发送请求: email={}, type={}", email, type);

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
                return; // 未知类型不抛异常，避免无意义重试
            }

            log.info("成功发送邮件到: {}, type={}", email, type);
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

            log.info("开始处理审核通知邮件发送请求: text={}", text);

            emailUtils.sendHtmlMailToAdmin(MailEnum.EXAMINE.getSubject(), MailEnum.EXAMINE.getTemplateName(),
                    Map.of("text", text, "frontendAdminHost", frontendAdminHost));

            log.info("成功发送审核通知邮件给管理员");
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

            log.info("开始处理友链审核通过邮件发送请求: email={}", email);

            emailUtils.sendHtmlMail(email, MailEnum.EXAMINE.getSubject(), MailEnum.EXAMINE.getTemplateName(),
                    Map.of("text", text, "frontendUserHost", frontendUserHost));

            log.info("成功发送友链审核通过邮件到: {}", email);
        } catch (Exception e) {
            log.error("处理友链审核通过邮件发送请求时出现异常, email={}, message={}", email, message, e);
            // 抛出异常触发重试机制
            throw new RuntimeException("友链审核通过邮件发送失败: " + e.getMessage(), e);
        }
    }

    /**
     * 监听访客记录队列
     * 处理访客记录插入（Redis去重 + 数据库插入）
     */
    @RabbitListener(queues = RabbitMQConstants.Visitor_Queue)
    public void receiveVisitorRecord(Map<String, Object> message) {
        try {
            Integer userId = (Integer) message.get("userId");
            String ip = (String) message.get("ip");
            String device = (String) message.get("device");

            // 1. 构建当天的 Redis Set Key（所有访客记录都存在一个 Set 中）
            String today = LocalDate.now().toString(); // 2025-10-07
            String setKey = RedisConstants.VisitorSet + today;

            // 2. 构建访客的唯一标识（Set 的成员）
            String visitorMember = userId != null
                    ? "user:" + userId + ":ip:" + ip + ":device:" + device
                    : "ip:" + ip + ":device:" + device;

            // 3. 【关键】先尝试添加到 Redis Set，利用 SADD 的原子性去重
            // SADD 返回值：1=成功添加（新元素），0=添加失败（元素已存在）
            Long addResult = redisTemplate.opsForSet().add(setKey, visitorMember);

            if (addResult == null || addResult == 0) {
                // Redis 中已存在，说明今日已记录过，直接跳过
                return;
            }

            // 4. 设置 Set 的过期时间（只在第一次添加时设置）
            // 使用 TTL 检查是否已设置过期时间，避免重复设置
            Long ttl = redisTemplate.getExpire(setKey, TimeUnit.SECONDS);
            if (ttl == null || ttl == -1) { // -1 表示没有设置过期时间
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
                long seconds = ChronoUnit.SECONDS.between(now, endOfDay);
                redisTemplate.expire(setKey, seconds, TimeUnit.SECONDS);
            }

            // 5. Redis 添加成功（说明是新访客），解析IP获取地理位置
            String address = ipUtils.getAddress(ip);

            // 6. 插入数据库（只有通过 Redis 原子去重的才会执行到这里）
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

}
