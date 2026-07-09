package cl.duoc.pagos_service.feign;
import cl.duoc.pagos_service.dto.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "cliente-service", path = "/api/v1/clientes")
public interface ClienteFeignClient {
    @GetMapping("/{id}")
    Map<String, Object> obtenerClienteRaw(@PathVariable("id") Long id);
}