package cl.duoc.carrito_service.feign;

import cl.duoc.carrito_service.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "producto-service")
public interface ProductoFeignClient {
    @GetMapping("/api/v1/productos/{id}")
    java.util.Map<String, Object> obtenerProductoRaw(@PathVariable("id") Long id);
}