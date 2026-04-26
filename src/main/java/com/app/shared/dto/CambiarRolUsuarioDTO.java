package com.app.shared.dto;

import com.app.usuario.model.Rol;
import jakarta.validation.constraints.NotNull;

public record CambiarRolUsuarioDTO(
        @NotNull Rol rol
) {
}
