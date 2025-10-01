package com.sidifensen.rabbitmq;

import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.enums.MailEnum;
import com.sidifensen.utils.EmailUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Value("${frontend.userHost}")
    private String frontendUserHost;

    @Value("${frontend.adminHost}")
    private String frontendAdminHost;

    // 注册,重置密码邮箱验证码
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(RabbitMQConstants.Email_Exchange),
            key = RabbitMQConstants.Email_Routing_Key,
            value = @Queue(RabbitMQConstants.Email_Queue)))
    public void receiveEmail(Map<String, Object> message) {
        try {
            log.info("========== 消费者接收到rabbitmq邮件发送请求 ==========");
            log.info("消息内容：{}", message);
            
            String email = (String) message.get("email");
            String checkCode = (String) message.get("checkCode");
            String type = (String) message.get("type");
            
            log.info("解析参数 - email: {}, checkCode: {}, type: {}", email, checkCode, type);

            if (type.equals(MailEnum.REGISTER.getType())) {
                log.info("匹配到注册邮件类型");
                emailUtils.sendHtmlMail(email, MailEnum.REGISTER.getSubject(), MailEnum.REGISTER.getTemplateName(), Map.of("checkCode", checkCode, "frontendUserHost", frontendUserHost));
            } else if (type.equals(MailEnum.RESET.getType())) {
                log.info("匹配到重置密码邮件类型");
                emailUtils.sendHtmlMail(email, MailEnum.RESET.getSubject(), MailEnum.RESET.getTemplateName(), Map.of("checkCode", checkCode, "frontendUserHost", frontendUserHost));
            } else if (type.equals(MailEnum.RESET_EMAIL.getType())) {
                log.info("匹配到重置邮箱邮件类型");
                emailUtils.sendHtmlMail(email, MailEnum.RESET_EMAIL.getSubject(), MailEnum.RESET_EMAIL.getTemplateName(), Map.of("checkCode", checkCode, "frontendUserHost", frontendUserHost));
            } else {
                log.warn("未知的邮件类型: {}", type);
            }
            log.info("成功发送邮箱给{}", email);
        } catch (Exception e) {
            log.error("处理邮件发送请求时出现异常", e);
            throw e;
        }
    }

    // 审核通知
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(RabbitMQConstants.Examine_Exchange),
            key = RabbitMQConstants.Examine_Routing_Key,
            value = @Queue(RabbitMQConstants.Examine_Queue)))
    public void receiveExamineEmail(Map<String, Object> message) {
        log.info("消费者接收到rabbitmq邮件发送请求：{}", message);

        String text = (String) message.get("text");
        emailUtils.sendHtmlMailToAdmin(MailEnum.EXAMINE.getSubject(), MailEnum.EXAMINE.getTemplateName(), Map.of("text", text, "frontendAdminHost", frontendAdminHost));

        log.info("成功发送邮箱给管理员");
    }

    // 友链审核通过通知
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(RabbitMQConstants.Link_Approval_Exchange),
            key = RabbitMQConstants.Link_Approval_Routing_Key,
            value = @Queue(RabbitMQConstants.Link_Approval_Queue)))
    public void receiveLinkApprovalEmail(Map<String, Object> message) {
        log.info("消费者接收到友链审核通过邮件发送请求：{}", message);

        String email = (String) message.get("email");
        String text = (String) message.get("text");

        emailUtils.sendHtmlMail(email, MailEnum.EXAMINE.getSubject(), MailEnum.EXAMINE.getTemplateName(),
                Map.of("text", text, "frontendUserHost", frontendUserHost));

        log.info("成功发送友链审核通过邮件给{}", email);
    }


}
