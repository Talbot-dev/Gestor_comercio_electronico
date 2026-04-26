package com.app.orden.model;

import com.app.producto.model.Producto;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity(name = "ordenItems")
@Table(name = "orden_items")
@Data
public class OrdenItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ordenItemId;

    private Integer quantity;
    private BigDecimal listPrice;

    @ManyToOne
    @JoinColumn(name = "ordenId")
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "productoId")
    private Producto product;
}
