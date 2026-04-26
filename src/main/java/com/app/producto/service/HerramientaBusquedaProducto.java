package com.app.producto.service;

import com.app.producto.model.Producto;

public interface HerramientaBusquedaProducto {
    Producto buscarProductoActivoPorId(Long id);
    boolean buscarDisponibilidad(Producto producto);
    String normalizarNombre(String nombre);
}
