package com.app.shared.mappers;

import com.app.orden.model.Orden;
import com.app.orden.model.OrdenStatus;
import com.app.shared.dto.CrearOrdenDTO;
import com.app.usuario.model.Usuario;

public class DtoToOrden {

    public static Orden mapearOrdenRequest(CrearOrdenDTO dto){
        Orden orden = new Orden();
        orden.setOrdenStatus(OrdenStatus.PENDING);
        orden.setTotalPrice(0L);
        return orden;
    }
}
