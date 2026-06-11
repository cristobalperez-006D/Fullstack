package cl.duoc.pagos_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagoDTO {
    private Long id;
    private Long pedidoId;
    private Long clienteId;
    private BigDecimal monto;
    private String metodoPago; // Ej: WEBPAY, TRANSFERENCIA
    private String estado;      // Ej: APROBADO, PENDIENTE, RECHAZADO
    private LocalDateTime fechaPago;
}
