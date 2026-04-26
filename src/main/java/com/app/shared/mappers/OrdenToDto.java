package com.app.shared.mappers;

import com.app.orden.model.Orden;
import com.app.shared.dto.OrdenResponseDTO;

import java.util.List;

public class OrdenToDto {

    public static OrdenResponseDTO mapearRespuesta(Orden orden) {
        List<OrdenResponseDTO.ItemOrdenResponseDTO> items = orden.getItems().stream()
                .map(item -> {
                    long subtotal = item.getListPrice().longValue() * item.getQuantity();
                    return new OrdenResponseDTO.ItemOrdenResponseDTO(
                            item.getProduct().getProductId(),
                            item.getQuantity(),
                            item.getListPrice().intValue(),
                            subtotal
                    );
                })
                .toList();

        return new OrdenResponseDTO(
                orden.getOrdenId(),
                orden.getUser().getUsuarioId(),
                orden.getOrdenStatus(),
                orden.getTotalPrice(),
                orden.getCreatedAt(),
                items
        );
    }
}
