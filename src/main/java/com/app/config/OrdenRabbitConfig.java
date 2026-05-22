package com.app.config;

import org.springframework.amqp.core.*;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OrdenRabbitConfig {

    public static final String EXCHANGE = "ecommerce";
    public static final String ROUTING_KEY = "orden.created";

    //Colas de compensación desde donde recibe mensajes
    public static final String PAYMENT_FAILED_STOCK_QUEUE = "q.payment.failed.stock";
    public static final String PAYMENT_FAILED_ORDER_QUEUE = "q.payment.failed.order";

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue paymentFailedStockQueue(){
        return QueueBuilder.durable(PAYMENT_FAILED_STOCK_QUEUE).build();
    }

    @Bean
    public Queue paymentFailedOrderQueue(){
        return QueueBuilder.durable(PAYMENT_FAILED_ORDER_QUEUE).build();
    }

    @Bean
    public Binding paymentFailedStockBinding(Queue paymentFailedStockQueue, TopicExchange exchange){
        return BindingBuilder.bind(paymentFailedStockQueue).to(exchange).with("payment.failed");
    }

    @Bean
    public Binding paymentFailedOrderBinding(Queue paymentFailedOrderQueue, TopicExchange exchange){
        return BindingBuilder.bind(paymentFailedOrderQueue).to(exchange).with("payment.failed");
    }

    @Bean
    public MessageConverter converter(){
        return new JacksonJsonMessageConverter();
    }
}

