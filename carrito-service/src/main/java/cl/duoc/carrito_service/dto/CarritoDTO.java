package cl.duoc.carrito_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarritoDTO {

    @Schema(description = "ID del carrito", example = "1")
    private Long id;

    @NotNull(message = "El ID del cliente es obligatorio")
    @Schema(description = "ID del cliente asociado", example = "1")
    private Long clienteId;

    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "ID del producto asociado", example = "101")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(description = "Cantidad de unidades", example = "2")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a cero")
    @Schema(description = "Precio unitario", example = "15990.00")
    private BigDecimal precioUnitario;

    // Estos objetos se completan vía Feign, así que en el POST
    // de entrada no son obligatorios, por eso les ponemos hidden=true
    @Schema(hidden = true)
    private ClienteDTO cliente;

    @Schema(hidden = true)
    private ProductoDTO producto;

    @Schema(description = "Subtotal calculado", example = "31980.00")
    private BigDecimal subtotal;
}