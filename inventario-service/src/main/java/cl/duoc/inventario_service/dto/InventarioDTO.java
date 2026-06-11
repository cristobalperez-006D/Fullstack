package cl.duoc.inventario_service.dto;

import lombok.Data;

@Data
public class InventarioDTO {
    private Long id;
    private Long productoId;
    private Integer cantidad;
}