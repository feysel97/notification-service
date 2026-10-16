package com.microservice.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.notification-queue}")
    private String notificationQueueName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange userExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(userExchange)
                .with(routingKey);
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(notificationQueueName).build();
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        // Ignores __TypeId__ header from auth-service so packages don't need to match
        typeMapper.setTypePrecedence(DefaultJackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
