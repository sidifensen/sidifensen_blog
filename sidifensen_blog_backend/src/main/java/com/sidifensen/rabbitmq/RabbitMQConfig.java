package com.sidifensen.rabbitmq;

import com.sidifensen.domain.constants.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Exchange emailExchange() {
        return new DirectExchange(RabbitMQConstants.Email_Exchange, true, false);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(RabbitMQConstants.Email_Queue, true, false, false);
    }

    @Bean
    public Binding bindingEmailQueueToExchange() {
        return BindingBuilder
                .bind(emailQueue())
                .to(emailExchange())
                .with(RabbitMQConstants.Email_Routing_Key).noargs();
    }

    @Bean
    public Exchange examineExchange() {
        return new DirectExchange(RabbitMQConstants.Examine_Exchange, true, false);
    }

    @Bean
    public Queue examineQueue() {
        return new Queue(RabbitMQConstants.Examine_Queue, true, false, false);
    }

    @Bean
    public Binding bindingExamineQueueToExchange() {
        return BindingBuilder
                .bind(examineQueue())
                .to(examineExchange())
                .with(RabbitMQConstants.Email_Routing_Key).noargs();
    }

    @Bean
    public Exchange linkApprovalExchange() {
        return new DirectExchange(RabbitMQConstants.Link_Approval_Exchange, true, false);
    }

    @Bean
    public Queue linkApprovalQueue() {
        return new Queue(RabbitMQConstants.Link_Approval_Queue, true, false, false);
    }

    @Bean
    public Binding bindingLinkApprovalQueueToExchange() {
        return BindingBuilder
                .bind(linkApprovalQueue())
                .to(linkApprovalExchange())
                .with(RabbitMQConstants.Link_Approval_Routing_Key).noargs();
    }

}
