package com.sidifensen.rabbitmq;

import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.OperationlogMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 操作日志 MQ 生产者
 * 负责将操作日志消息发送到 RabbitMQ
 *
 * @author sidifensen
 * @since 2025-07-08
 */
@Component
@Slf4j
public class OperationlogProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送操作日志消息到 MQ
     *
     * @param message 操作日志消息
     */
    public void sendOperationlogMessage(OperationlogMessage message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConstants.Operationlog_Exchange,
                    RabbitMQConstants.Operationlog_Routing_Key,
                    message
            );
            log.debug("发送操作日志到 MQ 成功，module: {}, operation: {}, operator: {}",
                    message.getModule(), message.getOperation(), message.getOperatorName());
        } catch (Exception e) {
            log.error("发送操作日志到 MQ 失败，module: {}, operation: {}",
                    message.getModule(), message.getOperation(), e);
            // MQ 发送失败不抛出异常，避免影响主业务
        }
    }
}
