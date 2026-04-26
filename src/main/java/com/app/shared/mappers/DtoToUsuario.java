package com.app.shared.mappers;

import com.app.shared.dto.CrearUsuarioDTO;
import com.app.usuario.model.Rol;
import com.app.usuario.model.Usuario;

public class DtoToUsuario {

    public static Usuario mapear(CrearUsuarioDTO dto, String passwordCodificado) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setPassword(passwordCodificado);
        usuario.setCiudad(dto.ciudad());
        usuario.setRol(Rol.CLIENTE);
        usuario.setIsActive(true);
        return usuario;
    }
}
