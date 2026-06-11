package cl.duoc.inventario_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventario")
@Data
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Le ponemos unique=true porque cada producto debería tener solo una fila de inventario
    @Column(nullable = false, unique = true)
    private Long productoId;

    @Column(nullable = false)
    private Integer cantidad;
}
