package com.app.producto.service;

import com.app.producto.model.Producto;
import com.app.producto.repository.ProductoRepository;
import com.app.shared.dto.ActualizarProductoDTO;
import com.app.shared.dto.CrearProductoDTO;
import com.app.shared.dto.ProductoResponseDTO;
import com.app.shared.mappers.DtoToProducto;
import com.app.shared.mappers.ProductoToDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final HerramientaBusquedaProducto busquedaProducto;


    // Buscar producto por ID
    public ProductoResponseDTO buscarProducto(Long id){
        return ProductoToDto.mapear(busquedaProducto.buscarProductoActivoPorId(id));
    }

    // Buscar producto por nombre
    public ProductoResponseDTO buscarProductoPorNombre(String nombre){
        String nombreProducto = busquedaProducto.normalizarNombre(nombre);
        Producto producto = productoRepository.findByName(nombreProducto)
                .filter(busquedaProducto::buscarDisponibilidad)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no existe."));
        return ProductoToDto.mapear(producto);
    }

    // Crear producto
    @Transactional
    public ProductoResponseDTO crearProducto(CrearProductoDTO dto) {
        String nombreNormalizado = busquedaProducto.normalizarNombre(dto.nombre());
        Producto productoExistente = productoRepository.findAnyByName(nombreNormalizado).orElse(null);

        if (productoExistente != null) {
            if (busquedaProducto.buscarDisponibilidad(productoExistente)) {
                throw new ResponseStatusException(CONFLICT, "Ya existe un producto con ese nombre.");
            }

            productoExistente.setName(nombreNormalizado);
            productoExistente.setPrice(dto.price());
            productoExistente.setStock(dto.stock());
            productoExistente.setIsActive(true);
            productoExistente.setDeletedAt(null);
            Producto restaurado = productoRepository.save(productoExistente);
            return ProductoToDto.mapear(restaurado);
        }

        Producto producto = DtoToProducto.mapear(dto);
        producto.setName(nombreNormalizado);
        Producto guardado = productoRepository.save(producto);
        return ProductoToDto.mapear(guardado);
    }

    // Editar producto
    @Transactional
    public ProductoResponseDTO editarProducto(Long id, ActualizarProductoDTO dto){
        Producto producto = busquedaProducto.buscarProductoActivoPorId(id);
        String nombreNormalizado = busquedaProducto.normalizarNombre(dto.nombre());

        Producto productoConMismoNombre = productoRepository.findAnyByName(nombreNormalizado).orElse(null);
        if (productoConMismoNombre != null && !productoConMismoNombre.getProductId().equals(id)) {
            throw new ResponseStatusException(CONFLICT, "Ya existe un producto con ese nombre.");
        }

        producto.setName(nombreNormalizado);
        producto.setPrice(dto.price());
        producto.setStock(dto.stock());
        Producto actualizado = productoRepository.save(producto);
        return ProductoToDto.mapear(actualizado);
    }

    // Eliminar producto
    @Transactional
    public void eliminarProducto(Long id){
        Producto producto = busquedaProducto.buscarProductoActivoPorId(id);
        productoRepository.delete(producto);
    }

    // Bloquear para garantizar integridad en consultas
    @Transactional
    public Producto bloquearParaConsulta (Long id){
        return productoRepository.findByIdForUpdate(id) // bloquea toda la fila del producto
                .filter(busquedaProducto::buscarDisponibilidad)
                .orElseThrow(() ->new ResponseStatusException(
                        NOT_FOUND, "No existe el producto con id "+id));
    }
}
