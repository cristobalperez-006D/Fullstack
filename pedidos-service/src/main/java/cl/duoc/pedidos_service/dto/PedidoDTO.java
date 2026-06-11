package cl.duoc.pedidos_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PedidoDTO {
    private Long id;
    private Long clienteId;
    private BigDecimal montoTotal;
    private String estado; // Ej: PENDIENTE, PAGADO, CANCELADO
    private LocalDateTime fechaPedido;

    // El objeto espejo que traeremos por Feign
    private ClienteDTO cliente;
}
