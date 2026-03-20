package com.sidifensen.rabbitmq;

import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.PayOrderExpireMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 订单超时 MQ 生产者
 * 负责发送订单超时消息到延迟队列
 */
@Component
@Slf4j
public class PayOrderExpireProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送订单超时消息到延迟队列
     * 消息会在队列中存活 15 分钟后自动转发到死信队列
     *
     * @param message 订单超时消息
     */
    public void sendExpireMessage(PayOrderExpireMessage message) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConstants.Order_Expire_Exchange,
                    RabbitMQConstants.Order_Expire_Routing_Key,
                    message
            );
            log.info("发送订单超时消息成功，orderNo={}", message.getOrderNo());
        } catch (Exception e) {
            log.error("发送订单超时消息失败，orderNo={}, error={}", message.getOrderNo(), e.getMessage(), e);
            // MQ 发送失败不抛出异常，订单仍可正常创建
        }
    }
}