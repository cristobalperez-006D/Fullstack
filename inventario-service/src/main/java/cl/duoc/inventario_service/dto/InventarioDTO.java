package cl.duoc.inventario_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventarioDTO {

    @Schema(description = "ID del registro de inventario", example = "1")
    private Long id;

    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "ID del producto al que pertenece este stock", example = "101")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad en inventario no puede ser negativa")
    @Schema(description = "Cantidad disponible en stock", example = "100")
    private Integer cantidad;
}