package cl.duoc.pagos_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificacionDTO {

    @Schema(description = "ID de la notificación", example = "1")
    private Long id;

    @NotNull(message = "El ID del cliente es obligatorio")
    @Schema(description = "ID del cliente que recibe la notificación", example = "1")
    private Long clienteId;

    @NotBlank(message = "El tipo de notificación es obligatorio")
    @Schema(description = "Tipo de notificación (EMAIL, SMS, PUSH)", example = "EMAIL")
    private String tipo;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 500, message = "El mensaje no puede superar los 500 caracteres")
    @Schema(description = "Contenido de la notificación", example = "Tu pedido ha sido enviado con éxito.")
    private String mensaje;

    @Schema(description = "Fecha y hora del envío", example = "2026-07-09T16:22:01")
    private LocalDateTime fechaEnvio;
}
