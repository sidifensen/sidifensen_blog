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
        log.info("消费者接收到rabbitmq邮件发送请求：{}", message);
        String email = (String) message.get("email");
        String checkCode = (String) message.get("checkCode");
        String type = (String) message.get("type");

        if (type.equals(MailEnum.REGISTER.getType())) {
            emailUtils.sendHtmlMail(email, MailEnum.REGISTER.getSubject(), MailEnum.REGISTER.getTemplateName(), Map.of("checkCode", checkCode, "frontendUserHost", frontendUserHost));
        } else if (type.equals(MailEnum.RESET.getType())) {
            emailUtils.sendHtmlMail(email, MailEnum.RESET.getSubject(), MailEnum.RESET.getTemplateName(), Map.of("checkCode", checkCode, "frontendUserHost", frontendUserHost));
        } else if (type.equals(MailEnum.EXAMINE.getType())) {
            emailUtils.sendHtmlMail(email, MailEnum.EXAMINE.getSubject(), MailEnum.EXAMINE.getTemplateName(), Map.of("text", checkCode, "frontendUserHost", frontendUserHost));
        }
        log.info("成功发送邮箱给{}", email);
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


}
