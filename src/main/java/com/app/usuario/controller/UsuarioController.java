package com.app.usuario.controller;

import com.app.shared.dto.CambiarRolUsuarioDTO;
import com.app.shared.dto.CrearUsuarioDTO;
import com.app.shared.dto.UsuarioResponseDTO;
import com.app.usuario.service.UsuarioService;
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
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Crear usuario nuevo
    @PostMapping("/api/public/registro/usuario")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(@RequestBody @Valid CrearUsuarioDTO usuario) {
        return new ResponseEntity<>(usuarioService.crearUsuario(usuario), HttpStatus.CREATED);
    }

    // Consultar usuario por ID
    @GetMapping("/api/usuario/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<UsuarioResponseDTO> consultarUsuario(@PathVariable @Min(1) Long id){
        return new ResponseEntity<>(usuarioService.buscarUsuario(id), HttpStatus.OK);
    }

    // Consultar usuario por nombre
    @GetMapping("/api/usuario/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<UsuarioResponseDTO> consultarUsuarioPorNombre(@PathVariable @NotBlank String nombre){
        return new ResponseEntity<>(usuarioService.buscarUsuarioPorNombre(nombre), HttpStatus.OK);
    }

    @PatchMapping("/api/usuario/{id}/rol")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> cambiarRolUsuario(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid CambiarRolUsuarioDTO dto
    ) {
        return ResponseEntity.ok(usuarioService.cambiarRol(id, dto));
    }

    // Eliminar un usuario (soft delete)
    @DeleteMapping("/api/usuario/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable @Min(1) Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
