package com.app.usuario.model;


import com.app.orden.model.Orden;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "usuario")
@Table(name = "usuario")
@SQLDelete(sql = "UPDATE usuario SET deleted_at = CURRENT_TIMESTAMP, is_active = false WHERE usuario_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private String ciudad;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fecha_registro;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "user")
    private List<Orden> ordenes;

}
