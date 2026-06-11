package cl.duoc.notificaciones_service.repository;

import cl.duoc.notificaciones_service.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByClienteId(Long clienteId);
}
