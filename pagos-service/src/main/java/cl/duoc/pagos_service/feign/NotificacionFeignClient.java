package cl.duoc.pagos_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "notificaciones-service", path = "/api/v1/notificaciones")
public interface NotificacionFeignClient {
    @PostMapping
    Map<String, Object> crear(@RequestBody Object dto); // Enviamos el objeto, recibimos el mapa
}