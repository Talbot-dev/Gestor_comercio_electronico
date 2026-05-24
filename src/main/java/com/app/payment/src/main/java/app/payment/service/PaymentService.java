package com.app.payment.src.main.java.app.payment.service;

import com.app.payment.src.main.java.app.payment.config.PaymentRabbitConfig;
import com.app.payment.src.main.java.app.payment.events.OrderCreatedEvent;
import com.app.payment.src.main.java.app.payment.events.PaymentApprovedEvent;
import com.app.payment.src.main.java.app.payment.events.PaymentRejectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final RabbitTemplate rabbitTemplate;

    public void simulatePayment(OrderCreatedEvent event) {
        int paymentResult = ThreadLocalRandom.current().nextInt(2);

        if (paymentResult == 1) {
            PaymentApprovedEvent approvedEvent = new PaymentApprovedEvent(
                    "PaymentProcessed",
                    String.valueOf(event.getOrderId()),
                    "Pago aprobado",
                    LocalDateTime.now());

            rabbitTemplate.convertAndSend(
                    PaymentRabbitConfig.EXCHANGE,
                    PaymentRabbitConfig.PAYMENT_PROCESSED_ROUTING_KEY,
                    approvedEvent);

            log.info("Pago aprobado para orderId={}", event.getOrderId());
            return;
        }

        PaymentRejectedEvent rejectedEvent = new PaymentRejectedEvent(
                "PaymentFailed",
                String.valueOf(event.getOrderId()),
                "Pago rechazado",
                LocalDateTime.now());

        rabbitTemplate.convertAndSend(
                PaymentRabbitConfig.EXCHANGE,
                PaymentRabbitConfig.PAYMENT_FAILED_ROUTING_KEY,
                rejectedEvent);

        log.info("Pago rechazado para orderId={}", event.getOrderId());
    }
}
