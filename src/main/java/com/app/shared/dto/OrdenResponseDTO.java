package com.app.shared.dto;

import java.time.LocalDate;
import java.util.List;

public record OrdenResponseDTO(
        Long orderId,
        Long usuarioId,
        Integer estado,
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
