package com.app.shared.events;

import com.app.orden.model.OrdenItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenCreatedEvent {
    private String orderType = "OrderCreated";
    private Long orderId;
    private Long userId;
    private LocalDateTime timestamp = LocalDateTime.now();
    private List<OrdenItem> items;

    public OrdenCreatedEvent(Long orderId, Long userId, List<OrdenItem> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
    }
}