package com.app.shared.mappers;

import com.app.shared.dto.UsuarioResponseDTO;
import com.app.usuario.model.Usuario;

public class UsuarioToDto {

    public static UsuarioResponseDTO mapearUsuarioToDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getUsuarioId(),
                usuario.getNombre(),
                usuario.getRol(),
                usuario.getCiudad(),
                usuario.getFecha_registro()
        );
    }
}
