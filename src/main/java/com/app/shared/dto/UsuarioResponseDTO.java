package com.app.shared.dto;

import com.app.usuario.model.Rol;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long usuarioId,
        String nombre,
        Rol rol,
        String ciudad,
        LocalDateTime fechaRegistro
) {
}
