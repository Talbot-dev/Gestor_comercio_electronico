package com.app.shared.dto;

import java.time.LocalDateTime;

public record ProductoResponseDTO(
        Long productoId,
        String nombre,
        Integer stock,
        Integer price,
        boolean isActive,
        LocalDateTime createdAt
) {
}
