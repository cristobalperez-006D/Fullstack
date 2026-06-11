package cl.duoc.notificaciones_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificacionDTO {
    private Long id;
    private Long clienteId;
    private String tipo; // Ej: EMAIL, SMS, PUSH
    private String mensaje;
    private LocalDateTime fechaEnvio;
}
