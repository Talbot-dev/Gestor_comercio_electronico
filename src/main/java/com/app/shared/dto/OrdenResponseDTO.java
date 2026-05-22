package com.app.shared.dto;

import com.app.orden.model.OrdenStatus;

import java.time.LocalDate;
import java.util.List;

public record OrdenResponseDTO(
        Long orderId,
        Long usuarioId,
        OrdenStatus estado,
        Long totalPrice,
        LocalDate createdAt,
        List<ItemOrdenResponseDTO> items
) {
    public record ItemOrdenResponseDTO(
            Long productoId,
            Integer cantidad,
            Integer precioUnitario,
            Long subtotal
    ) {
    }
}
