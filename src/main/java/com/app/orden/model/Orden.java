package com.app.orden.model;

import com.app.usuario.model.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "orden")
@Data
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ordenId;

    private Integer ordenStatus;

    @CreationTimestamp
    private LocalDate createdAt;
    private Long totalPrice;

    @ManyToOne
    @JoinColumn(name = "UsuarioId")
    private Usuario user;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL)
    private List<OrdenItem> items;
}
