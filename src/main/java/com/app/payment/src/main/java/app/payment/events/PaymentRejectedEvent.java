package com.app.payment.src.main.java.app.payment.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRejectedEvent {
	private String eventType;
	private String orderId;
	private String reason;
	private LocalDateTime timestamp;
}
