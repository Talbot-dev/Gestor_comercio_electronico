package com.app.orden.consumer;

import com.app.config.OrdenRabbitConfig;
import com.app.orden.service.OrdenService;
import com.app.shared.events.PaymentRejectedOrderEvent;
import com.app.shared.events.PaymentRejectedStockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompensationHandlers {

    private final OrdenService ordenService;

    @RabbitListener(queues = OrdenRabbitConfig.PAYMENT_FAILED_STOCK_QUEUE)
    public void compensationStockFailed(PaymentRejectedStockEvent evento) {
        log.info("");
        ordenService.devolverStock(evento.getOrderId());
    }

    @RabbitListener(queues = OrdenRabbitConfig.PAYMENT_FAILED_ORDER_QUEUE)
    public void compensationOrderFailed(PaymentRejectedOrderEvent evento) {
        log.info("");
        ordenService.cancelarOrden(evento.getOrderId());
    }
}
