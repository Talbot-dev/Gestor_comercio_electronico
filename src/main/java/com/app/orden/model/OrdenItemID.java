package com.app.orden.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@Data
@EqualsAndHashCode
public class OrdenItemID implements Serializable {
    @Column(name = "ordenId")
    private Long ordenId;

    @Column(name = "productoId")
    private Long productoId;
}
