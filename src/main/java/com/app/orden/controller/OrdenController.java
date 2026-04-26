package com.app.orden.controller;

import com.app.orden.service.OrdenService;
import com.app.shared.dto.CrearOrdenDTO;
import com.app.shared.dto.OrdenResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class OrdenController {

    private final OrdenService ordenService;

    @PostMapping("/api/orden")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<OrdenResponseDTO> crearOrden(@RequestBody @Valid CrearOrdenDTO peticionDTO) {
        return new ResponseEntity<>(ordenService.crearPeticionOrden(peticionDTO), HttpStatus.OK);
    }

    @GetMapping("/api/orden/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<List<OrdenResponseDTO>> consultarHistorialPorUsuario(@PathVariable @Min(1) Long usuarioId) {
        return ResponseEntity.ok(ordenService.consultarHistorialPorUsuario(usuarioId));
    }
}
