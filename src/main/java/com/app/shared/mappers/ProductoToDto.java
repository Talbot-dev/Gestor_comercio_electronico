package com.app.shared.mappers;

import com.app.producto.model.Producto;
import com.app.shared.dto.ProductoResponseDTO;

public class ProductoToDto {

    public static ProductoResponseDTO mapear(Producto producto){
        return new ProductoResponseDTO(
                producto.getProductId(),
                producto.getName(),
                producto.getStock(),
                producto.getPrice(),
                Boolean.TRUE.equals(producto.getIsActive()),
                producto.getCreatedAt()
        );
    }
}
