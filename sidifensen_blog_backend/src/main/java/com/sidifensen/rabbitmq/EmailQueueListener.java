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
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author sidifensen
 * @since 2025-07-09
 */
@Component
@Slf4j
public class EmailQueueListener {

    @Resource
    private EmailUtils emailUtils;

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
            emailUtils.sendHtmlMail(email, MailEnum.REGISTER.getSubject(), MailEnum.REGISTER.getTemplateName(), Map.of("checkCode", checkCode));
        }else if (type.equals(MailEnum.RESET.getType())) {
            emailUtils.sendHtmlMail(email, MailEnum.RESET.getSubject(), MailEnum.RESET.getTemplateName(), Map.of("checkCode", checkCode));
        }
        log.info("成功发送邮箱给{}", email);
    }
}
