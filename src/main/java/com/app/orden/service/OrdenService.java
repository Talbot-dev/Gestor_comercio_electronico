package com.app.orden.service;

import com.app.orden.model.Orden;
import com.app.orden.model.OrdenItem;
import com.app.orden.model.OrdenItemID;
import com.app.orden.repository.OrdenRepository;
import com.app.producto.model.Producto;
import com.app.producto.service.ProductoService;
import com.app.shared.dto.CrearOrdenDTO;
import com.app.shared.dto.OrdenResponseDTO;
import com.app.shared.mappers.OrdenToDto;
import com.app.shared.mappers.DtoToOrden;
import com.app.usuario.model.Usuario;
import com.app.usuario.service.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class OrdenService {

    private static final Logger log = LoggerFactory.getLogger(OrdenService.class);

    private final OrdenRepository ordenRepository;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    /**
     * Procesa y persiste una nueva orden a partir de los datos del DTO recibido.
     *
     * @param dto datos de la orden a crear, incluyendo el usuario y la lista de ítems.
     * @return {@link OrdenResponseDTO} con los datos de la orden persistida.
     * @throws ResponseStatusException si el usuario no existe, algún producto no tiene
     *  stock o la cantidad solicitada supera el disponible.
     */
    @Transactional
    public OrdenResponseDTO crearPeticionOrden(CrearOrdenDTO dto) {
        if (dto.items() == null || dto.items().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "La orden debe contener al menos un producto");
        }

        // Verifica que el usuario exista antes de continuar; lanza excepción si no se encuentra.
        Usuario usuario = usuarioService.buscarUsuarioPorId(dto.usuarioId());

        // Mapea el DTO a la entidad Orden y le asigna el usuario.
        Orden orden = DtoToOrden.mapearOrdenRequest(dto);
        orden.setUser(usuario);
        orden.setItems(new ArrayList<>());

        // Unifica los ítems del DTO sumando cantidades de productos con el mismo ID.
        Map<Long, Integer> productosSolicitados = combinarCantidades(dto.items());

        /*
         * Ordena los ID de producto de forma ascendente.
         * Esto garantiza que todas las transacciones concurrentes bloqueen los recursos
         * en el mismo orden, eliminando la posibilidad de deadlock.
         */
        List<Long> productIdsOrdenados = productosSolicitados.keySet().stream()
                .sorted(Comparator.naturalOrder())
                .toList();

        // Bloquea los productos (pessimistic block)
        // para garantizar consistencia en la actualización del stock
        List<Producto> productosBloqueadosPorConcurrencia = new ArrayList<>();
        for (Long productId : productIdsOrdenados) {
            Producto producto = productoService.bloquearParaConsulta(productId);
            productosBloqueadosPorConcurrencia.add(producto);
        }

        List<OrdenItem> items = new ArrayList<>();
        long precioTotalDeLaOrden = 0L;

        Orden ordenGuardada = ordenRepository.save(orden);

        for (Producto producto : productosBloqueadosPorConcurrencia) {
            Integer cantidadSolicitada = productosSolicitados.get(producto.getProductId());

            // Lanza excepción si el stock es nulo, cero o insuficiente para la cantidad pedida.
            Integer stockActual = validarStockActual(producto, cantidadSolicitada);

            // Descuenta la cantidad solicitada del stock disponible.
            producto.setStock(stockActual - cantidadSolicitada);

            OrdenItem item = new OrdenItem();
            item.setOrden(ordenGuardada);
            item.setProduct(producto);
            item.setQuantity(cantidadSolicitada);
            item.setListPrice(BigDecimal.valueOf(producto.getPrice()));
            items.add(item);

            precioTotalDeLaOrden += (long) producto.getPrice() * cantidadSolicitada;
        }

        ordenGuardada.setItems(items);
        ordenGuardada.setTotalPrice(precioTotalDeLaOrden);
        ordenGuardada = ordenRepository.save(ordenGuardada);

        log.info("AUDIT orden_creada ordenId={} usuarioId={} total={} items={}",
                ordenGuardada.getOrdenId(),
                ordenGuardada.getUser().getUsuarioId(),
                ordenGuardada.getTotalPrice(),
                ordenGuardada.getItems().size());

        return OrdenToDto.mapearRespuesta(ordenGuardada);
    }

    @Transactional
    public List<OrdenResponseDTO> consultarHistorialPorUsuario(Long usuarioId) {
        usuarioService.buscarUsuarioActivoPorId(usuarioId);
        return ordenRepository.findAllByUserUsuarioIdOrderByCreatedAtDescOrdenIdDesc(usuarioId).stream()
                .map(OrdenToDto::mapearRespuesta)
                .toList();
    }

    /**
     *
     *  Consolida la lista de ítems del DTO sumando las cantidades de aquellos
     *  que referencian el mismo producto.
     * Si los productos recibidos son {productId:1, quantity:3}, {productId:1, quantity:5}
     * usando {@link LinkedHashMap} mantiene el orden de inserción y combina cantidades del mismo id.
     *
     * {productId:1, quantity:(5+3) = 8}
     *
     * @param items lista de ítems del DTO, potencialmente con ID duplicados.
     * @return mapa de productoId → cantidad total consolidada.
     * */
    private Map<Long, Integer> combinarCantidades(List<CrearOrdenDTO.ItemOrdenDTO> items) {
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (CrearOrdenDTO.ItemOrdenDTO item : items) {
            cantidades.merge(item.productoId(), item.cantidad(), Integer::sum);
        }
        return cantidades;
    }

    /**
     * Válida que el producto tenga stock suficiente para cubrir la cantidad solicitada.
     *
     * @param producto el producto cuyo stock se va a verificar.
     * @param cantidadSolicitada la cantidad requerida por la orden.
     * @return el stock actual del producto si la validación es exitosa.
     * @throws ResponseStatusException con estado {@code 409 CONFLICT} si el stock es
     *  nulo, cero, o menor a la cantidad solicitada.
     */
    private static @NonNull Integer validarStockActual(Producto producto, Integer cantidadSolicitada) {
        Integer stockActual = producto.getStock();

        if (stockActual == null || stockActual < 1) {
            throw new ResponseStatusException(
                    CONFLICT, "El producto " + producto.getProductId() + " no tiene stock disponible");
        }

        if (cantidadSolicitada > stockActual) {
            throw new ResponseStatusException(
                    CONFLICT,
                    "Stock insuficiente para el producto " + producto.getProductId()
                            + ". Disponible: " + stockActual + ", solicitado: " + cantidadSolicitada);
        }
        return stockActual;
    }

}
