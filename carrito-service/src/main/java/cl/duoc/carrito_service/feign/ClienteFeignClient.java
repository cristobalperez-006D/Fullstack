package cl.duoc.carrito_service.feign;

import cl.duoc.carrito_service.dto.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cliente-service")
public interface ClienteFeignClient {
    @GetMapping("/api/v1/clientes/{id}")
    java.util.Map<String, Object> obtenerClienteRaw(@PathVariable("id") Long id);
}