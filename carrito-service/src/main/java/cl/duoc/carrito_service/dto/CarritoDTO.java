package cl.duoc.carrito_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarritoDTO {
    private Long id;
    private Long clienteId;
    private Long productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;

    // Aquí le chantamos los objetos que traeremos por Feign
    private ClienteDTO cliente;
    private ProductoDTO producto;

    private BigDecimal subtotal;
}
