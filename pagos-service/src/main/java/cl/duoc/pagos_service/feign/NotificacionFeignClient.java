package cl.duoc.pagos_service.feign;

import cl.duoc.pagos_service.dto.NotificacionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
@FeignClient(name = "notificaciones-service", path = "/api/notificaciones")
public interface NotificacionFeignClient {
    @PostMapping
    NotificacionDTO crear(@RequestBody NotificacionDTO dto);
}

