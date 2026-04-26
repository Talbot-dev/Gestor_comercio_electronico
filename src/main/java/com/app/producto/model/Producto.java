package com.app.producto.model;

import com.app.orden.model.OrdenItem;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "producto")
@Table(name = "producto")
@SQLDelete(sql = "UPDATE producto SET deleted_at = CURRENT_TIMESTAMP, is_active = false WHERE product_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "product")
    private List<OrdenItem> items;
}
