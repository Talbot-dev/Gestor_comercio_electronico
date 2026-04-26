package com.app;

import com.app.usuario.model.Usuario;
import com.app.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limpiarDatos() {
        jdbcTemplate.execute("DELETE FROM orden_items");
        jdbcTemplate.execute("DELETE FROM orden");
        jdbcTemplate.execute("DELETE FROM producto");
        jdbcTemplate.execute("DELETE FROM usuario");
    }

    @Test
    void creaYConsultaUsuarioPorIdYNombre() throws Exception {
        mockMvc.perform(post("/api/public/registro/usuario")
                        .contentType("application/json")
                        .content(jsonUsuario("juan", "secreta", "Bogota")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("juan"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));

        Long usuarioId = usuarioRepository.findByNombreIgnoreCase("juan").orElseThrow().getUsuarioId();

        mockMvc.perform(get("/api/public/usuario/{id}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(usuarioId));

        mockMvc.perform(get("/api/public/usuario/nombre/{nombre}", "juan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("juan"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminaUsuarioConSoftDeleteYPermiteRestaurarloPorRegistro() throws Exception {
        Usuario usuario = crearUsuario("laura", "Bogota");

        mockMvc.perform(delete("/api/usuario/{id}", usuario.getUsuarioId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/public/usuario/{id}", usuario.getUsuarioId()))
                .andExpect(status().isNotFound());

        Optional<Usuario> oculto = usuarioRepository.findAnyByNombreIgnoreCase("laura");
        assertTrue(oculto.isPresent());
        assertNotNull(oculto.get().getDeletedAt());
        assertFalse(Boolean.TRUE.equals(oculto.get().getIsActive()));

        mockMvc.perform(post("/api/public/registro/usuario")
                        .contentType("application/json")
                        .content(jsonUsuario("laura", "otra-clave", "Medellin")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value(usuario.getUsuarioId()))
                .andExpect(jsonPath("$.nombre").value("laura"))
                .andExpect(jsonPath("$.ciudad").value("Medellin"));

        Usuario restaurado = usuarioRepository.findById(usuario.getUsuarioId()).orElseThrow();
        assertTrue(Boolean.TRUE.equals(restaurado.getIsActive()));
        assertEquals("Medellin", restaurado.getCiudad());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPuedeCambiarRolDeUsuario() throws Exception {
        Usuario usuario = crearUsuario("mario", "Cali");

        mockMvc.perform(patch("/api/usuario/{id}/rol", usuario.getUsuarioId())
                        .contentType("application/json")
                        .content("""
                                {
                                  "rol": "ADMIN"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(usuario.getUsuarioId()))
                .andExpect(jsonPath("$.rol").value("ADMIN"));

        Usuario actualizado = usuarioRepository.findById(usuario.getUsuarioId()).orElseThrow();
        assertEquals(com.app.usuario.model.Rol.ADMIN, actualizado.getRol());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeCambiarRolDeUsuario() throws Exception {
        Usuario usuario = crearUsuario("pepe", "Cali");

        mockMvc.perform(patch("/api/usuario/{id}/rol", usuario.getUsuarioId())
                        .contentType("application/json")
                        .content("""
                                {
                                  "rol": "ADMIN"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    private Usuario crearUsuario(String nombre, String ciudad) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setPassword("secreta");
        usuario.setCiudad(ciudad);
        usuario.setRol(com.app.usuario.model.Rol.CLIENTE);
        usuario.setIsActive(true);
        return usuarioRepository.save(usuario);
    }

    private String jsonUsuario(String nombre, String password, String ciudad) {
        return """
                {
                  "nombre": "%s",
                  "password": "%s",
                  "ciudad": "%s"
                }
                """.formatted(nombre, password, ciudad);
    }
}
