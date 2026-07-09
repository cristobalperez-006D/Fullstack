package cl.duoc.envios_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EnvioDTO {

    @Schema(description = "ID del envío", example = "1")
    private Long id;

    @NotNull(message = "El ID del pedido es obligatorio")
    @Schema(description = "ID del pedido asociado", example = "500")
    private Long pedidoId;

    @NotBlank(message = "La dirección de destino es obligatoria")
    @Size(min = 10, max = 200, message = "La dirección debe tener entre 10 y 200 caracteres")
    @Schema(description = "Dirección de entrega", example = "Av. Vicuña Mackenna 1234, Santiago")
    private String direccionDestino;

    @Schema(description = "Estado actual del envío", example = "PENDIENTE")
    private String estado;

    @Schema(description = "Código de seguimiento único", example = "TRACK-123456789")
    private String codigoSeguimiento;
}