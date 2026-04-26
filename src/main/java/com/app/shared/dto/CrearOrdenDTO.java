package com.app.shared.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CrearOrdenDTO(
        @NotNull Long usuarioId,
        @NotEmpty List<@Valid ItemOrdenDTO> items
) {
    public record ItemOrdenDTO(
            @NotNull Long productoId,
            @NotNull @Min(1) Integer cantidad
    ) {
    }
}
