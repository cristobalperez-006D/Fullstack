package cl.duoc.pedidos_service.repository;

import cl.duoc.pedidos_service.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Para ver todo el historial de pedidos de un puro cliente
    List<Pedido> findByClienteId(Long clienteId);
}
