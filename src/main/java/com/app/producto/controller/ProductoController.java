package com.app.producto.controller;

import com.app.producto.service.ProductoService;
import com.app.shared.dto.ActualizarProductoDTO;
import com.app.shared.dto.CrearProductoDTO;
import com.app.shared.dto.ProductoResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // Buscar producto
    @GetMapping("/api/productos/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<ProductoResponseDTO> consultarProducto(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok().body(productoService.buscarProducto(id));
    }

    // Buscar producto por nombre
    @GetMapping("/api/productos/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<ProductoResponseDTO> consultarProductoPorNombre(@PathVariable @NotBlank String nombre) {
        return ResponseEntity.ok().body(productoService.buscarProductoPorNombre(nombre));
    }

    // Crear producto
    @PostMapping("/api/productos")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ProductoResponseDTO> crearProducto(@RequestBody @Valid CrearProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crearProducto(dto));
    }

    // Editar producto
    @PutMapping("/api/productos/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ProductoResponseDTO> modificarInformacionProducto(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid ActualizarProductoDTO dto
    ){
        return ResponseEntity.ok().body(productoService.editarProducto(id, dto));
    }

    // Eliminar producto (soft delete)
    @DeleteMapping("/api/productos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> eliminarProducto(@PathVariable @Min(1) Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
