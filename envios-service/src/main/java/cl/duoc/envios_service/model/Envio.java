package cl.duoc.envios_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "envios")
@Data
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId;

    @Column(nullable = false)
    private String direccionDestino;

    @Column(nullable = false)
    private String estado;

    @Column(unique = true)
    private String codigoSeguimiento;
}
