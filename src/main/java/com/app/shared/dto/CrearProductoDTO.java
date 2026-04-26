package com.app.shared.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearProductoDTO(
        @NotBlank String nombre,
        @NotNull @Min(1) Integer price,
        @NotNull @Min(0) Integer stock
) {
}
