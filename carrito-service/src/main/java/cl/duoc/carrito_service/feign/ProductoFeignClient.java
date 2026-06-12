package cl.duoc.carrito_service.feign;

import cl.duoc.carrito_service.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// ¡QUITAMOS el atributo 'path'!
@FeignClient(name = "producto-service")
public interface ProductoFeignClient {

    // Ponemos la ruta completa aquí
    @GetMapping("/api/productos/{id}")
    ProductoDTO obtenerProductoPorId(@PathVariable("id") Long id);
}