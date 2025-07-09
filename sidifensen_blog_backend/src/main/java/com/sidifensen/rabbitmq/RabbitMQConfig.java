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

}
