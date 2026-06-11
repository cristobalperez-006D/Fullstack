package cl.duoc.inventario_service.repository;

import cl.duoc.inventario_service.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    // Fundamental pa' buscar el stock directo por el ID del producto
    Inventario findByProductoId(Long productoId);
}
