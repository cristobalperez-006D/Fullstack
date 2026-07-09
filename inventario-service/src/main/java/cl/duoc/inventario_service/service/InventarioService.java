package cl.duoc.inventario_service.service;

import cl.duoc.inventario_service.dto.InventarioDTO;
import cl.duoc.inventario_service.model.Inventario;
import cl.duoc.inventario_service.repository.InventarioRepository;
import cl.duoc.inventario_service.exception.RecursoNoEncontradoException; // Importa tu excepción
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    public List<InventarioDTO> findAll() {
        return inventarioRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public InventarioDTO findByProductoId(Long productoId) {
        Inventario inv = inventarioRepository.findByProductoId(productoId);
        if (inv == null) {
            throw new RecursoNoEncontradoException("No existe registro de inventario para el producto ID: " + productoId);
        }
        return mapToDTO(inv);
    }

    public InventarioDTO save(InventarioDTO dto) {
        Inventario inv = new Inventario();
        inv.setProductoId(dto.getProductoId());
        inv.setCantidad(dto.getCantidad());

        Inventario guardado = inventarioRepository.save(inv);
        return mapToDTO(guardado);
    }

    public InventarioDTO restarStock(Long productoId, Integer cantidad) {
        Inventario inv = inventarioRepository.findByProductoId(productoId);

        // 1. Validar si el producto existe en inventario
        if (inv == null) {
            throw new RecursoNoEncontradoException("Producto no encontrado en inventario: " + productoId);
        }

        // 2. Validar stock suficiente
        if (inv.getCantidad() < cantidad) {
            throw new RecursoNoEncontradoException("Stock insuficiente para el producto: " + productoId);
        }

        // 3. Ejecutar operación
        inv.setCantidad(inv.getCantidad() - cantidad);
        return mapToDTO(inventarioRepository.save(inv));
    }

    public void delete(Long id) {
        if (!inventarioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No se puede eliminar: Inventario ID " + id + " no encontrado.");
        }
        inventarioRepository.deleteById(id);
    }

    private InventarioDTO mapToDTO(Inventario inv) {
        InventarioDTO dto = new InventarioDTO();
        dto.setId(inv.getId());
        dto.setProductoId(inv.getProductoId());
        dto.setCantidad(inv.getCantidad());
        return dto;
    }

    public InventarioDTO sumarStock(Long productoId, Integer cantidad) {
        Inventario inv = inventarioRepository.findByProductoId(productoId);
        if (inv == null) {
            throw new RecursoNoEncontradoException("No existe registro para sumar stock del producto: " + productoId);
        }
        inv.setCantidad(inv.getCantidad() + cantidad);
        return mapToDTO(inventarioRepository.save(inv));
    }

    public boolean tieneStockCritico(Long productoId) {
        Inventario inv = inventarioRepository.findByProductoId(productoId);
        if (inv == null) {
            throw new RecursoNoEncontradoException("Producto no encontrado: " + productoId);
        }
        return inv.getCantidad() < 5;
    }
}