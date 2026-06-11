package cl.duoc.notificaciones_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false, length = 500) // Le damos más espacio al mensaje por si es largo
    private String mensaje;

    @Column(nullable = false)
    private LocalDateTime fechaEnvio;
}
