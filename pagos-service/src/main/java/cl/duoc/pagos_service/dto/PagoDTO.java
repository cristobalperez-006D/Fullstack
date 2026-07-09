package cl.duoc.pagos_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagoDTO {

    @Schema(description = "ID del pago", example = "1")
    private Long id;

    @NotNull(message = "El ID del pedido es obligatorio")
    @Schema(description = "ID del pedido asociado", example = "500")
    private Long pedidoId;

    @NotNull(message = "El ID del cliente es obligatorio")
    @Schema(description = "ID del cliente que realiza el pago", example = "1")
    private Long clienteId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    @Schema(description = "Monto pagado", example = "25990.00")
    private BigDecimal monto;

    @NotBlank(message = "El método de pago es obligatorio")
    @Schema(description = "Método de pago (WEBPAY, TRANSFERENCIA)", example = "WEBPAY")
    private String metodoPago;

    @Schema(description = "Estado del pago", example = "APROBADO")
    private String estado;

    @Schema(description = "Fecha y hora del pago", example = "2026-07-09T16:23:00")
    private LocalDateTime fechaPago;

    @Schema(description = "Datos del cliente asociados al pago")
    private ClienteDTO cliente;
}