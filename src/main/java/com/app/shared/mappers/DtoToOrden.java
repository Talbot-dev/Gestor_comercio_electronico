package com.app.shared.mappers;

import com.app.orden.model.Orden;
import com.app.shared.dto.CrearOrdenDTO;
import com.app.usuario.model.Usuario;

public class DtoToOrden {

    public static Orden mapearOrdenRequest(CrearOrdenDTO dto){
        Orden orden = new Orden();
        Usuario usuario = new Usuario();
        usuario.setUsuarioId(dto.usuarioId());
        orden.setUser(usuario);
        orden.setOrdenStatus(1);
        orden.setTotalPrice(0L);
        return orden;
    }
}
