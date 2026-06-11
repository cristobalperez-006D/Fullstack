package cl.duoc.envios_service.dto;

import lombok.Data;

@Data
public class EnvioDTO {
    private Long id;
    private Long pedidoId;
    private String direccionDestino;
    private String estado; // Ej: PENDIENTE, EN_CAMINO, ENTREGADO
    private String codigoSeguimiento;
}