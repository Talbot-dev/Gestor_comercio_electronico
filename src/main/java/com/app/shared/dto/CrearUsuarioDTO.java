package com.app.shared.dto;

import jakarta.validation.constraints.NotBlank;

public record CrearUsuarioDTO(
        @NotBlank String nombre,
        @NotBlank String password,
        @NotBlank String ciudad
) {
}
