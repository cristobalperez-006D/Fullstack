package cl.duoc.carrito_service.feign;

import cl.duoc.carrito_service.dto.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// ¡Acá estaba el drama! Ahora apunta a la ruta correcta que hace match con el gateway
@FeignClient(name = "cliente-service", path = "/api/clientes")
public interface ClienteFeignClient {

    @GetMapping("/{id}")
    ClienteDTO obtenerClientePorId(@PathVariable("id") Long id);
}