package com.app.payment.src.main.java.app.payment.consumer;

import com.app.payment.src.main.java.app.payment.config.PaymentRabbitConfig;
import com.app.payment.src.main.java.app.payment.events.OrderCreatedEvent;
import com.app.payment.src.main.java.app.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCreatedListener {

    private final PaymentService paymentService;

    @RabbitListener(queues = PaymentRabbitConfig.ORDER_CREATED_QUEUE)
    public void processOrderCreated(OrderCreatedEvent evento) {
        log.info("Evento order.created recibido para orderId={}", evento.getOrderId());
        paymentService.simulatePayment(evento);
    }
}
