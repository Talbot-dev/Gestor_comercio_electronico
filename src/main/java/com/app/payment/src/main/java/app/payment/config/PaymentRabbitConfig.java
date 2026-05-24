package com.app.payment.src.main.java.app.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentRabbitConfig {

    public static final String EXCHANGE = "ecommerce";
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
    public static final String PAYMENT_PROCESSED_ROUTING_KEY = "payment.processed";
    public static final String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";

    // Cola de flujo principal
    public static final String ORDER_CREATED_QUEUE = "q.order.created";

    @Bean(name = "paymentExchange")
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean(name = "paymentOrderCreatedQueue")
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(ORDER_CREATED_QUEUE).build();
    }

    @Bean
    public Binding bindOrderCreated(@Qualifier("paymentOrderCreatedQueue") Queue orderCreatedQueue,
                                    @Qualifier("paymentExchange") TopicExchange exchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(exchange).with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean(name = "paymentMessageConverter")
    public MessageConverter converter() {
        return new JacksonJsonMessageConverter();
    }
}