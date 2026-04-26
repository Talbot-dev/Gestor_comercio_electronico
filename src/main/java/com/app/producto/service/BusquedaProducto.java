package com.app.producto.service;

import com.app.producto.model.Producto;
import com.app.producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BusquedaProducto implements HerramientaBusquedaProducto{

    private final ProductoRepository productoRepository;

    @Override
    public Producto buscarProductoActivoPorId(Long id){
        return productoRepository.findById(id)
                .filter(this::buscarDisponibilidad)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no existe."));
    }

    @Override
    public boolean buscarDisponibilidad(Producto producto){
        return producto.getDeletedAt() == null && producto.getIsActive().equals(true);
    }

    @Override
    public String normalizarNombre(String nombre){
        return nombre == null ? null : nombre.trim();
    }
}
