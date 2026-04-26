package com.app.usuario.service;

import com.app.shared.dto.CambiarRolUsuarioDTO;
import com.app.shared.dto.CrearUsuarioDTO;
import com.app.shared.dto.UsuarioResponseDTO;
import com.app.shared.mappers.DtoToUsuario;
import com.app.shared.mappers.UsuarioToDto;
import com.app.usuario.model.Rol;
import com.app.usuario.model.Usuario;
import com.app.usuario.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    //Registrar usuario nuevo
    @Transactional
    public UsuarioResponseDTO crearUsuario(CrearUsuarioDTO dto) {
        String nombreNormalizado = normalizarNombre(dto.nombre());
        Usuario usuarioExistente = usuarioRepositorio.findAnyByNombreIgnoreCase(nombreNormalizado).orElse(null);

        if (usuarioExistente != null) {
            if (estaActivo(usuarioExistente)) {
                throw new ResponseStatusException(CONFLICT, "Ya existe un usuario con nombre " + nombreNormalizado);
            }

            usuarioExistente.setNombre(nombreNormalizado);
            usuarioExistente.setPassword(passwordEncoder.encode(dto.password()));
            usuarioExistente.setCiudad(dto.ciudad().trim());
            usuarioExistente.setRol(Rol.CLIENTE);
            usuarioExistente.setIsActive(true);
            usuarioExistente.setDeletedAt(null);
            Usuario restaurado = usuarioRepositorio.save(usuarioExistente);
            return UsuarioToDto.mapearUsuarioToDTO(restaurado);
        }

        String passwordCodificado = passwordEncoder.encode(dto.password());
        Usuario usuario = DtoToUsuario.mapear(dto, passwordCodificado);
        usuario.setNombre(nombreNormalizado);
        usuario.setCiudad(dto.ciudad().trim());
        Usuario guardado = usuarioRepositorio.save(usuario);
        return UsuarioToDto.mapearUsuarioToDTO(guardado);
    }

    // Respuesta de búsqueda por ID al controlador
    public UsuarioResponseDTO buscarUsuario(Long id) {
        return UsuarioToDto.mapearUsuarioToDTO(buscarUsuarioActivoPorId(id));
    }

    // Respuesta de búsqueda por nombre al controlador
    public UsuarioResponseDTO buscarUsuarioPorNombre(String nombre) {
        return UsuarioToDto.mapearUsuarioToDTO(buscarPorNombre(nombre));
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = buscarUsuarioActivoPorId(id);
        usuarioRepositorio.delete(usuario);
    }

    @Transactional
    public UsuarioResponseDTO cambiarRol(Long id, CambiarRolUsuarioDTO dto) {
        Usuario usuario = buscarUsuarioActivoPorId(id);
        usuario.setRol(dto.rol());
        Usuario actualizado = usuarioRepositorio.save(usuario);
        return UsuarioToDto.mapearUsuarioToDTO(actualizado);
    }

    //Buscar un usuario por ID
    public Usuario buscarUsuarioPorId(Long id) {
        return buscarUsuarioActivoPorId(id);
    }

    public Usuario buscarUsuarioActivoPorId(Long id) {
        return usuarioRepositorio.findById(id)
                .filter(this::estaActivo)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "No existe el usuario con id " + id));
    }

    // Buscar un usuario por nombre
    public Usuario buscarPorNombre(String nombre) {
        return usuarioRepositorio.findByNombreIgnoreCase(normalizarNombre(nombre))
                .filter(this::estaActivo)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "No existe el usuario con nombre " + nombre));
    }

    private boolean estaActivo(Usuario usuario) {
        return usuario.getDeletedAt() == null && Boolean.TRUE.equals(usuario.getIsActive());
    }

    private String normalizarNombre(String nombre) {
        return nombre == null ? null : nombre.trim();
    }

}
