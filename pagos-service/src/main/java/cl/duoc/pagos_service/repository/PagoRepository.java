package cl.duoc.pagos_service.repository;

import cl.duoc.pagos_service.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    // Por si necesitas rescatar los pagos asociados a un pedido específico
    List<Pago> findByPedidoId(Long pedidoId);
    List<Pago> findByClienteId(Long clienteId);

}
