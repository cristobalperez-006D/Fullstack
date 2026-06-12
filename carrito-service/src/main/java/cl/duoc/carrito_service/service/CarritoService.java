package cl.duoc.carrito_service.service;

import cl.duoc.carrito_service.dto.CarritoDTO;
import cl.duoc.carrito_service.dto.ClienteDTO;
import cl.duoc.carrito_service.dto.ProductoDTO;
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

        // Subtotal joya con BigDecimal
        if (carrito.getPrecioUnitario() != null && carrito.getCantidad() != null) {
            dto.setSubtotal(carrito.getPrecioUnitario().multiply(BigDecimal.valueOf(carrito.getCantidad())));
        }

        try {
            ClienteDTO cliente = clienteFeignClient.obtenerClientePorId(carrito.getClienteId());
            dto.setCliente(cliente);
        } catch (Exception e) {
            System.out.println("Fallo al traer cliente: " + e.getMessage());
        }

        try {
            ProductoDTO producto = productoFeignClient.obtenerProductoPorId(carrito.getProductoId());
            dto.setProducto(producto);
        } catch (Exception e) {
            System.out.println("Fallo al traer producto: " + e.getMessage());
        }

        return dto;
    }
}