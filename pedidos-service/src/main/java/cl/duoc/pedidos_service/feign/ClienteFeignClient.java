package cl.duoc.pedidos_service.feign;

import cl.duoc.pedidos_service.dto.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cliente-service", path = "/api/clientes")
public interface ClienteFeignClient {

    @GetMapping("/{id}")
    ClienteDTO obtenerClientePorId(@PathVariable("id") Long id);
}