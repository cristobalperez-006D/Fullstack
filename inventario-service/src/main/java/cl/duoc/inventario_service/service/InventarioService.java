package cl.duoc.inventario_service.service;

import cl.duoc.inventario_service.dto.InventarioDTO;
import cl.duoc.inventario_service.model.Inventario;
import cl.duoc.inventario_service.repository.InventarioRepository;
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
        return inv != null ? mapToDTO(inv) : null;
    }

    public InventarioDTO save(InventarioDTO dto) {
        Inventario inv = new Inventario();
        inv.setProductoId(dto.getProductoId());
        inv.setCantidad(dto.getCantidad());

        Inventario guardado = inventarioRepository.save(inv);
        return mapToDTO(guardado);
    }

    // Este método es la joyita: resta el stock si es que hay suficiente
    public InventarioDTO restarStock(Long productoId, Integer cantidadComprada) {
        Inventario inv = inventarioRepository.findByProductoId(productoId);

        if (inv != null && inv.getCantidad() >= cantidadComprada) {
            inv.setCantidad(inv.getCantidad() - cantidadComprada);
            Inventario actualizado = inventarioRepository.save(inv);
            return mapToDTO(actualizado);
        }
        // Si no hay stock o el producto no existe en inventario, devolvemos null
        return null;
    }

    public void delete(Long id) {
        inventarioRepository.deleteById(id);
    }

    // El clásico mapeador pa' tener todo en casa
    private InventarioDTO mapToDTO(Inventario inv) {
        InventarioDTO dto = new InventarioDTO();
        dto.setId(inv.getId());
        dto.setProductoId(inv.getProductoId());
        dto.setCantidad(inv.getCantidad());
        return dto;
    }
}
