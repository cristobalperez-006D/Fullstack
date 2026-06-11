package cl.duoc.carrito_service.repository;

import cl.duoc.carrito_service.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    List<Carrito> findByClienteId(Long clienteId);

    // Para ver si el loco ya metió ese producto al carro antes y solo sumarle cantidad
    Optional<Carrito> findByClienteIdAndProductoId(Long clienteId, Long productoId);

    // Para cuando el usuario pague y tengamos que vaciarle el carro por completo
    void deleteByClienteId(Long clienteId);
}
