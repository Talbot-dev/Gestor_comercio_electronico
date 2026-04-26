package com.app.shared.mappers;

import com.app.producto.model.Producto;
import com.app.shared.dto.CrearProductoDTO;

public class DtoToProducto {

    public static Producto mapear(CrearProductoDTO dto){
        Producto producto = new Producto();
        producto.setName(dto.nombre());
        producto.setPrice(dto.price());
        producto.setStock(dto.stock());
        producto.setIsActive(true);
        return producto;

    }
}
