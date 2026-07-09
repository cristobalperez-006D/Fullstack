package cl.duoc.carrito_service.service;

import cl.duoc.carrito_service.dto.CarritoDTO;
import cl.duoc.carrito_service.dto.ClienteDTO;
import cl.duoc.carrito_service.dto.ProductoDTO;
import cl.duoc.carrito_service.exception.RecursoNoEncontradoException;
import cl.duoc.carrito_service.feign.ClienteFeignClient;
import cl.duoc.carrito_service.feign.ProductoFeignClient;
import cl.duoc.carrito_service.model.Carrito;
import cl.duoc.carrito_service.repository.CarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ClienteFeignClient clienteFeignClient;

    @Autowired
    private ProductoFeignClient productoFeignClient;

    public CarritoDTO agregarItem(CarritoDTO dto) {
        Optional<Carrito> itemExistente = carritoRepository
                .findByClienteIdAndProductoId(dto.getClienteId(), dto.getProductoId());

        Carrito itemGuardado;
        if (itemExistente.isPresent()) {
            Carrito item = itemExistente.get();
            item.setCantidad(item.getCantidad() + dto.getCantidad());
            itemGuardado = carritoRepository.save(item);
        } else {
            Carrito nuevoItem = new Carrito();
            nuevoItem.setClienteId(dto.getClienteId());
            nuevoItem.setProductoId(dto.getProductoId());
            nuevoItem.setCantidad(dto.getCantidad());
            nuevoItem.setPrecioUnitario(dto.getPrecioUnitario());

            itemGuardado = carritoRepository.save(nuevoItem);
        }

        return mapToDTO(itemGuardado);
    }

    public List<CarritoDTO> obtenerCarritoPorCliente(Long clienteId) {
        return carritoRepository.findByClienteId(clienteId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Agrega esto en tu clase CarritoService.java
    public List<CarritoDTO> obtenerTodos() {
        return carritoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void eliminarItem(Long itemId) {
        if (!carritoRepository.existsById(itemId)) {
            throw new RecursoNoEncontradoException("No se encontró el ítem con ID: " + itemId);
        }
        carritoRepository.deleteById(itemId);
    }

    public void vaciarCarrito(Long clienteId) {
        carritoRepository.deleteByClienteId(clienteId);
    }

    private CarritoDTO mapToDTO(Carrito carrito) {
        CarritoDTO dto = new CarritoDTO();
        dto.setId(carrito.getId());
        dto.setClienteId(carrito.getClienteId());
        dto.setProductoId(carrito.getProductoId());
        dto.setCantidad(carrito.getCantidad());
        dto.setPrecioUnitario(carrito.getPrecioUnitario());

        if (carrito.getPrecioUnitario() != null && carrito.getCantidad() != null) {
            dto.setSubtotal(carrito.getPrecioUnitario().multiply(BigDecimal.valueOf(carrito.getCantidad())));
        }

        // --- REPARACIÓN BRÍGIDA ---
        try {
            // Obtenemos un mapa genérico en lugar del DTO directo
            java.util.Map<String, Object> resp = clienteFeignClient.obtenerClienteRaw(carrito.getClienteId());
            if (resp.containsKey("data")) {
                java.util.LinkedHashMap<String, Object> data = (java.util.LinkedHashMap<String, Object>) resp.get("data");
                ClienteDTO cliente = new ClienteDTO();
                cliente.setId(Long.valueOf(data.get("id").toString()));
                cliente.setNombre((String) data.get("nombre"));
                cliente.setEmail((String) data.get("email"));
                dto.setCliente(cliente);
            }
        } catch (Exception e) {
            dto.setCliente(null);
        }

        try {
            java.util.Map<String, Object> resp = productoFeignClient.obtenerProductoRaw(carrito.getProductoId());
            if (resp.containsKey("data")) {
                java.util.LinkedHashMap<String, Object> data = (java.util.LinkedHashMap<String, Object>) resp.get("data");
                ProductoDTO prod = new ProductoDTO();
                prod.setId(Long.valueOf(data.get("id").toString()));
                prod.setNombre((String) data.get("nombre"));
                prod.setPrecio(new BigDecimal(data.get("precio").toString()));
                dto.setProducto(prod);
            }
        } catch (Exception e) {
            dto.setProducto(null);
        }

        return dto;
    }
    public boolean existeEnCarrito(Long clienteId, Long productoId) {
        return carritoRepository.findByClienteIdAndProductoId(clienteId, productoId).isPresent();
    }

    public BigDecimal calcularTotalCarrito(Long clienteId) {
        List<Carrito> items = carritoRepository.findByClienteId(clienteId);
        return items.stream()
                .map(i -> i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}