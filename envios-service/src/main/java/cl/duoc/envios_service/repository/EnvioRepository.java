package cl.duoc.envios_service.repository;

import cl.duoc.envios_service.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvioRepository extends JpaRepository<Envio, Long> {
    // Un par de búsquedas personalizadas por si las moscas
    Envio findByPedidoId(Long pedidoId);
    Envio findByCodigoSeguimiento(String codigoSeguimiento);
}
