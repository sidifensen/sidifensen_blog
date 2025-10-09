package com.sidifensen.rabbitmq;

import com.sidifensen.domain.constants.RabbitMQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * RabbitMQ 配置类
 * 功能：
 * 1. 配置队列、交换机、绑定关系
 * 2. 配置死信队列（DLQ）用于处理重试失败的消息
 * 3. 配置消息持久化
 * 4. 配置发布者确认和返回机制
 */
@Configuration
@Slf4j
public class RabbitMQConfig {

    // ==================== 常量配置 ====================

    /**
     * 死信队列配置参数
     * 统一管理死信交换机和路由键的配置，所有业务队列共用此配置
     */
    private static final Map<String, Object> DEAD_LETTER_ARGS = Map.of(
            "x-dead-letter-exchange", RabbitMQConstants.Dead_Letter_Exchange,
            "x-dead-letter-routing-key", RabbitMQConstants.Dead_Letter_Routing_Key
    );

    // ==================== 死信队列配置 ====================

    /**
     * 死信交换机
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(RabbitMQConstants.Dead_Letter_Exchange, true, false);
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(RabbitMQConstants.Dead_Letter_Queue)
                .build();
    }

    /**
     * 绑定死信队列到死信交换机
     */
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(RabbitMQConstants.Dead_Letter_Routing_Key);
    }

    // ==================== 邮件队列配置 ====================

    @Bean
    public DirectExchange emailExchange() {
        // 参数：name, durable(持久化), autoDelete(自动删除)
        return new DirectExchange(RabbitMQConstants.Email_Exchange, true, false);
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(RabbitMQConstants.Email_Queue)
                .withArguments(DEAD_LETTER_ARGS)
                .build();
    }

    @Bean
    public Binding bindingEmailQueueToExchange() {
        return BindingBuilder
                .bind(emailQueue())
                .to(emailExchange())
                .with(RabbitMQConstants.Email_Routing_Key);
    }

    // ==================== 审核队列配置 ====================

    @Bean
    public DirectExchange examineExchange() {
        return new DirectExchange(RabbitMQConstants.Examine_Exchange, true, false);
    }

    @Bean
    public Queue examineQueue() {
        return QueueBuilder.durable(RabbitMQConstants.Examine_Queue)
                .withArguments(DEAD_LETTER_ARGS)
                .build();
    }

    @Bean
    public Binding bindingExamineQueueToExchange() {
        return BindingBuilder
                .bind(examineQueue())
                .to(examineExchange())
                .with(RabbitMQConstants.Examine_Routing_Key);
    }

    // ==================== 友链审核队列配置 ====================

    @Bean
    public DirectExchange linkApprovalExchange() {
        return new DirectExchange(RabbitMQConstants.Link_Approval_Exchange, true, false);
    }

    @Bean
    public Queue linkApprovalQueue() {
        return QueueBuilder.durable(RabbitMQConstants.Link_Approval_Queue)
                .withArguments(DEAD_LETTER_ARGS)
                .build();
    }

    @Bean
    public Binding bindingLinkApprovalQueueToExchange() {
        return BindingBuilder
                .bind(linkApprovalQueue())
                .to(linkApprovalExchange())
                .with(RabbitMQConstants.Link_Approval_Routing_Key);
    }

    // ==================== 访客记录队列配置 ====================

    @Bean
    public DirectExchange visitorExchange() {
        return new DirectExchange(RabbitMQConstants.Visitor_Exchange, true, false);
    }

    @Bean
    public Queue visitorQueue() {
        return QueueBuilder.durable(RabbitMQConstants.Visitor_Queue)
                .withArguments(DEAD_LETTER_ARGS)
                .build();
    }

    @Bean
    public Binding bindingVisitorQueueToExchange() {
        return BindingBuilder
                .bind(visitorQueue())
                .to(visitorExchange())
                .with(RabbitMQConstants.Visitor_Routing_Key);
    }

    // ==================== RabbitTemplate 配置 ====================

    /**
     * 配置消息转换器（使用 JSON 格式）
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置 RabbitTemplate
     * 1. 设置消息转换器
     * 2. 设置发布者确认回调
     * 3. 设置发布者返回回调
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);

        // 设置发布者确认回调（消息成功到达交换机时触发）
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("消息发送失败到交换机, correlationData: {}, cause: {}", correlationData, cause);
            }
        });

        // 设置发布者返回回调（消息无法路由到队列时触发）
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("消息无法路由到队列: exchange={}, routingKey={}, replyText={}, message={}",
                    returned.getExchange(),
                    returned.getRoutingKey(),
                    returned.getReplyText(),
                    returned.getMessage());
        });

        // 设置消息持久化（默认）
        rabbitTemplate.setMandatory(true);

        return rabbitTemplate;
    }

}
