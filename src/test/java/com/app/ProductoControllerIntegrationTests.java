package com.app;

import com.app.producto.model.Producto;
import com.app.producto.repository.ProductoRepository;
import com.app.shared.dto.ActualizarProductoDTO;
import com.app.shared.dto.CrearProductoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductoControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

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
    void creaYBuscaProductoPorIdYNombre() throws Exception {
        CrearProductoDTO dto = new CrearProductoDTO("Teclado mecanico", 250, 8);

        mockMvc.perform(post("/api/public/productos")
                        .contentType("application/json")
                        .content(jsonProducto(dto.nombre(), dto.price(), dto.stock())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Teclado mecanico"))
                .andExpect(jsonPath("$.price").value(250))
                .andExpect(jsonPath("$.stock").value(8));

        Long productId = productoRepository.findByNameIgnoreCase("Teclado mecanico")
                .orElseThrow()
                .getProductId();

        mockMvc.perform(get("/api/public/productos/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(productId))
                .andExpect(jsonPath("$.nombre").value("Teclado mecanico"));

        mockMvc.perform(get("/api/public/productos/nombre/{nombre}", "Teclado mecanico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(productId))
                .andExpect(jsonPath("$.stock").value(8));
    }

    @Test
    void rechazaCreacionConNombreDuplicadoActivo() throws Exception {
        productoRepository.save(crearProducto("Mouse gamer", 10, 120));

        CrearProductoDTO dto = new CrearProductoDTO("Mouse gamer", 150, 3);

        mockMvc.perform(post("/api/public/productos")
                        .contentType("application/json")
                        .content(jsonProducto(dto.nombre(), dto.price(), dto.stock())))
                .andExpect(status().isConflict())
                .andExpect(result -> assertEquals("Ya existe un producto con ese nombre.",
                        result.getResponse().getContentAsString()));
    }

    @Test
    void editaProductoExistente() throws Exception {
        Producto producto = productoRepository.save(crearProducto("Monitor 24", 4, 900));
        ActualizarProductoDTO dto = new ActualizarProductoDTO("Monitor 27", 1100, 6);

        mockMvc.perform(put("/api/public/productos/{id}", producto.getProductId())
                        .contentType("application/json")
                        .content(jsonProducto(dto.nombre(), dto.price(), dto.stock())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Monitor 27"))
                .andExpect(jsonPath("$.price").value(1100))
                .andExpect(jsonPath("$.stock").value(6));

        Producto actualizado = productoRepository.findById(producto.getProductId()).orElseThrow();
        assertEquals("Monitor 27", actualizado.getName());
        assertEquals(1100, actualizado.getPrice());
        assertEquals(6, actualizado.getStock());
    }

    @Test
    void eliminaProductoConSoftDeleteYPermiteRestaurarloPorNombre() throws Exception {
        Producto producto = productoRepository.save(crearProducto("Camara", 5, 700));

        mockMvc.perform(delete("/api/public/productos/{id}", producto.getProductId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/public/productos/{id}", producto.getProductId()))
                .andExpect(status().isNotFound());

        Optional<Producto> oculto = productoRepository.findAnyByNameIgnoreCase("Camara");
        assertTrue(oculto.isPresent());
        assertNotNull(oculto.get().getDeletedAt());
        assertFalse(Boolean.TRUE.equals(oculto.get().getIsActive()));

        CrearProductoDTO restaurar = new CrearProductoDTO("Camara", 950, 9);

        mockMvc.perform(post("/api/public/productos")
                        .contentType("application/json")
                        .content(jsonProducto(restaurar.nombre(), restaurar.price(), restaurar.stock())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productoId").value(producto.getProductId()))
                .andExpect(jsonPath("$.price").value(950))
                .andExpect(jsonPath("$.stock").value(9))
                .andExpect(jsonPath("$.isActive").value(true));

        Producto restaurado = productoRepository.findById(producto.getProductId()).orElseThrow();
        assertEquals("Camara", restaurado.getName());
        assertEquals(950, restaurado.getPrice());
        assertEquals(9, restaurado.getStock());
        assertTrue(Boolean.TRUE.equals(restaurado.getIsActive()));
        assertNull(restaurado.getDeletedAt());
    }

    private Producto crearProducto(String nombre, int stock, int precio) {
        Producto producto = new Producto();
        producto.setName(nombre);
        producto.setStock(stock);
        producto.setPrice(precio);
        producto.setIsActive(true);
        return producto;
    }

    private String jsonProducto(String nombre, int price, int stock) {
        return """
                {
                  "nombre": "%s",
                  "price": %d,
                  "stock": %d
                }
                """.formatted(nombre, price, stock);
    }
}
