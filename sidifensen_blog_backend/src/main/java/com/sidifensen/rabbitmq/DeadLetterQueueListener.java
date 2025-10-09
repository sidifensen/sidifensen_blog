package com.sidifensen.rabbitmq;

import com.sidifensen.domain.constants.RabbitMQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 死信队列监听器
 * 功能：
 * 1. 监听死信队列，处理重试失败的消息
 * 2. 记录错误日志，便于排查问题
 * 3. 可以发送告警通知管理员
 * 
 * @author sidifensen
 * @since 2025-10-06
 */
@Component
@Slf4j
public class DeadLetterQueueListener {

    /**
     * 监听死信队列
     * 当消息重试多次失败后，会进入死信队列
     * 需要人工介入处理
     * 
     * @param message 死信消息
     */
    @RabbitListener(queues = RabbitMQConstants.Dead_Letter_Queue)
    public void handleDeadLetterMessage(Message message) {
        try {
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
            
            // 获取消息头信息
            String exchange = message.getMessageProperties().getReceivedExchange();
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            Long deliveryTag = message.getMessageProperties().getDeliveryTag();
            
            // 获取重试次数（如果有）
            Integer retryCount = (Integer) message.getMessageProperties().getHeaders().get("x-retry-count");
            
            // 记录详细的错误日志
            log.error("============ 死信消息 ============");
            log.error("时间: {}", new Date());
            log.error("来源交换机: {}", exchange);
            log.error("来源路由键: {}", routingKey);
            log.error("投递标签: {}", deliveryTag);
            log.error("重试次数: {}", retryCount);
            log.error("消息内容: {}", messageBody);
            log.error("消息头: {}", message.getMessageProperties().getHeaders());
            log.error("================================");
            
            // TODO: 这里可以添加告警通知功能
            // 1. 发送邮件通知管理员
            // 2. 发送钉钉/企业微信通知
            // 3. 存储到数据库供后续人工处理
            
            // 示例：根据不同的队列来源做不同处理
            if (routingKey != null) {
                if (routingKey.contains("email")) {
                    handleEmailDeadLetter(messageBody);
                } else if (routingKey.contains("examine")) {
                    handleExamineDeadLetter(messageBody);
                } else if (routingKey.contains("link")) {
                    handleLinkApprovalDeadLetter(messageBody);
                } else {
                    handleUnknownDeadLetter(messageBody);
                }
            }
            
        } catch (Exception e) {
            log.error("处理死信消息时发生异常", e);
            // 死信队列处理失败也不抛出异常，避免死循环
        }
    }
    
    /**
     * 处理邮件死信
     */
    private void handleEmailDeadLetter(String messageBody) {
        log.error("邮件发送失败，已达最大重试次数，消息内容: {}", messageBody);
        // TODO: 可以存储到数据库，定期重试或人工介入
    }
    
    /**
     * 处理审核通知死信
     */
    private void handleExamineDeadLetter(String messageBody) {
        log.error("审核通知发送失败，已达最大重试次数，消息内容: {}", messageBody);
        // TODO: 可以存储到数据库，定期重试或人工介入
    }
    
    /**
     * 处理友链审核死信
     */
    private void handleLinkApprovalDeadLetter(String messageBody) {
        log.error("友链审核通知发送失败，已达最大重试次数，消息内容: {}", messageBody);
        // TODO: 可以存储到数据库，定期重试或人工介入
    }
    
    /**
     * 处理未知类型死信
     */
    private void handleUnknownDeadLetter(String messageBody) {
        log.error("未知类型消息失败，已达最大重试次数，消息内容: {}", messageBody);
    }
    
}

