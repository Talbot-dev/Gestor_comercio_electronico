package com.app.orden.consumer;

import com.app.config.OrdenRabbitConfig;
import com.app.orden.service.OrdenService;
import com.app.shared.events.PaymentRejectedOrderEvent;
import com.app.shared.events.PaymentRejectedStockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompensationHandlers {

    private final OrdenService ordenService;

    @RabbitListener(queues = OrdenRabbitConfig.PAYMENT_PROCESSED_QUEUE)
    public void paymentApproved(Map<String, Object> evento) {
        Object orderIdRaw = evento.get("orderId");
        if (orderIdRaw == null) {
            log.warn("Evento payment.processed recibido sin orderId: {}", evento);
            return;
        }

        Long orderId = Long.valueOf(String.valueOf(orderIdRaw));
        log.info("Evento payment.processed recibido para orderId={}", orderId);
        ordenService.confirmarPago(orderId);
    }

    @RabbitListener(queues = OrdenRabbitConfig.PAYMENT_FAILED_STOCK_QUEUE)
    public void compensationStockFailed(PaymentRejectedStockEvent evento) {
        log.info("Evento payment.failed (stock) recibido para orderId={}", evento.getOrderId());
        ordenService.devolverStock(evento.getOrderId());
    }

    @RabbitListener(queues = OrdenRabbitConfig.PAYMENT_FAILED_ORDER_QUEUE)
    public void compensationOrderFailed(PaymentRejectedOrderEvent evento) {
        log.info("Evento payment.failed (orden) recibido para orderId={}", evento.getOrderId());
        String cancelacion = ordenService.cancelarOrden(evento.getOrderId());
        ordenService.resolverPagoRechazado(evento.getOrderId(), cancelacion);
    }
}
