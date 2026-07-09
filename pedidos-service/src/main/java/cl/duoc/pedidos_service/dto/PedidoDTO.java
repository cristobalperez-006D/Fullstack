package cl.duoc.pedidos_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PedidoDTO {

    @Schema(description = "ID del pedido", example = "500")
    private Long id;

    @NotNull(message = "El ID del cliente es obligatorio")
    @Schema(description = "ID del cliente que realiza el pedido", example = "1")
    private Long clienteId;

    @NotNull(message = "El monto total es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto total debe ser mayor a cero")
    @Schema(description = "Monto total del pedido", example = "59990.00")
    private BigDecimal montoTotal;

    @Schema(description = "Estado actual del pedido", example = "PENDIENTE")
    private String estado;

    @Schema(description = "Fecha y hora del pedido", example = "2026-07-09T16:23:55")
    private LocalDateTime fechaPedido;

    // Al igual que en el carrito, este objeto se completa por Feign
    @Schema(hidden = true)
    private ClienteDTO cliente;
}