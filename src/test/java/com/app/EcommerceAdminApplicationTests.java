package com.app;

import com.app.orden.repository.OrdenRepository;
import com.app.orden.service.OrdenService;
import com.app.producto.model.Producto;
import com.app.producto.repository.ProductoRepository;
import com.app.shared.dto.CrearOrdenDTO;
import com.app.shared.dto.OrdenResponseDTO;
import com.app.usuario.model.Rol;
import com.app.usuario.model.Usuario;
import com.app.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class EcommerceAdminApplicationTests {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OrdenRepository orderRepository;

    @BeforeEach
    void limpiarDatos() {
        orderRepository.deleteAll();
        productoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void creaOrdenYDescuentaStock() {
        Usuario usuario = crearUsuario("Juan");
        Producto teclado = crearProducto("Teclado", 5, 100);
        Producto mouse = crearProducto("Mouse", 2, 50);

        CrearOrdenDTO dto = new CrearOrdenDTO(
                usuario.getUsuarioId(),
                List.of(
                        new CrearOrdenDTO.ItemOrdenDTO(teclado.getProductId(), 2),
                        new CrearOrdenDTO.ItemOrdenDTO(mouse.getProductId(), 1)
                )
        );

        OrdenResponseDTO respuesta = ordenService.crearPeticionOrden(dto);

        assertNotNull(respuesta.orderId());
        assertEquals(usuario.getUsuarioId(), respuesta.usuarioId());
        assertEquals(250L, respuesta.totalPrice());
        assertEquals(3, productoRepository.findById(teclado.getProductId()).orElseThrow().getStock());
        assertEquals(1, productoRepository.findById(mouse.getProductId()).orElseThrow().getStock());
        assertEquals(1, orderRepository.count());
    }

    @Test
    void rechazaOrdenSinProductos() {
        Usuario usuario = crearUsuario("Ana");

        CrearOrdenDTO dto = new CrearOrdenDTO(usuario.getUsuarioId(), List.of());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> ordenService.crearPeticionOrden(dto)
        );

        assertEquals(400, exception.getStatusCode().value());
        assertEquals(0, orderRepository.count());
    }

    @Test
    void rechazaOrdenConStockInsuficienteSinPersistirCambios() {
        Usuario usuario = crearUsuario("Luisa");
        Producto producto = crearProducto("Monitor", 1, 900);

        CrearOrdenDTO dto = new CrearOrdenDTO(
                usuario.getUsuarioId(),
                List.of(new CrearOrdenDTO.ItemOrdenDTO(producto.getProductId(), 2))
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> ordenService.crearPeticionOrden(dto)
        );

        assertEquals(409, exception.getStatusCode().value());
        assertEquals(1, productoRepository.findById(producto.getProductId()).orElseThrow().getStock());
        assertEquals(0, orderRepository.count());
    }

    @Test
    void consultaHistorialDeOrdenesPorUsuario() {
        Usuario usuario = crearUsuario("Carlos");
        Producto teclado = crearProducto("Teclado Pro", 10, 100);
        Producto mouse = crearProducto("Mouse Pro", 10, 50);

        ordenService.crearPeticionOrden(new CrearOrdenDTO(
                usuario.getUsuarioId(),
                List.of(new CrearOrdenDTO.ItemOrdenDTO(teclado.getProductId(), 1))
        ));

        ordenService.crearPeticionOrden(new CrearOrdenDTO(
                usuario.getUsuarioId(),
                List.of(new CrearOrdenDTO.ItemOrdenDTO(mouse.getProductId(), 2))
        ));

        List<OrdenResponseDTO> historial = ordenService.consultarHistorialPorUsuario(usuario.getUsuarioId());

        assertEquals(2, historial.size());
        assertEquals(usuario.getUsuarioId(), historial.getFirst().usuarioId());
        assertTrue(historial.stream().allMatch(orden -> orden.usuarioId().equals(usuario.getUsuarioId())));
    }

    private Usuario crearUsuario(String nombre) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setPassword("secreta");
        usuario.setCiudad("Bogota");
        usuario.setRol(Rol.CLIENTE);
        usuario.setIsActive(true);
        return usuarioRepository.save(usuario);
    }

    private Producto crearProducto(String nombre, int stock, int precio) {
        Producto producto = new Producto();
        producto.setName(nombre);
        producto.setStock(stock);
        producto.setPrice(precio);
        producto.setIsActive(true);
        return productoRepository.save(producto);
    }
}
