package cl.duoc.carrito_service.feign;

import cl.duoc.carrito_service.dto.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cliente-service", path = "/api/v1/clientes")
public interface ClienteFeignClient {

    @GetMapping("/{id}")
    ClienteDTO obtenerClientePorId(@PathVariable("id") Long id);
}
