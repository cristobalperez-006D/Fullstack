package cl.duoc.cliente_service.controller;
import cl.duoc.cliente_service.dto.ClienteDTO;
import cl.duoc.cliente_service.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Gestión de Identidad de Clientes", description = "Servicio core para la administración, resguardo y consulta del repositorio de usuarios. Gestiona el perfil completo del cliente y la integridad de sus datos personales.")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Operation(
            summary = "Extraer directorio de clientes",
            description = "Obtiene la lista consolidada de todos los perfiles de clientes registrados en la base de datos central. Implementado para servicios de CRM y reportes de usuario."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Directorio de clientes recuperado satisfactoriamente.")
    })
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> obtenerTodos() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @Operation(
            summary = "Recuperar perfil de cliente por ID",
            description = "Ejecuta un lookup de alta precisión para extraer los datos maestros de un usuario específico, fundamental para la validación de identidad en otros microservicios vía Feign."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil del cliente encontrado."),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado en el repositorio de identidades.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerPorId(
            @Parameter(description = "Identificador único del usuario", example = "42")
            @PathVariable Long id
    ) {
        ClienteDTO cliente = clienteService.findById(id);
        return cliente != null ? ResponseEntity.ok(cliente) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Aprovisionar nuevo cliente",
            description = "Registra un nuevo usuario en el ecosistema, persistiendo su información personal y configuraciones iniciales. Gatilla la creación del perfil lógico del consumidor."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente aprovisionado e integrado al sistema."),
            @ApiResponse(responseCode = "400", description = "Error en la estructura del DTO de entrada.")
    })
    @PostMapping
    public ResponseEntity<ClienteDTO> crear(
            @Parameter(description = "Objeto con la metadata del cliente a registrar")
            @RequestBody ClienteDTO dto
    ) {
        ClienteDTO creado = clienteService.save(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Actualizar datos maestros del perfil",
            description = "Permite la modificación de atributos del usuario mediante una operación transaccional, asegurando que la información de contacto y personal esté siempre sincronizada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Cliente no localizado para realizar la actualización.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizar(
            @Parameter(description = "ID del cliente a modificar", example = "42")
            @PathVariable Long id,
            @RequestBody ClienteDTO dto
    ) {
        ClienteDTO actualizado = clienteService.update(id, dto);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Baja de cliente en el sistema",
            description = "Ejecuta la eliminación o deshabilitación del perfil de un usuario, cumpliendo con los protocolos de depuración de registros en el sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Perfil de cliente eliminado de la base de datos.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del cliente a purgar", example = "42")
            @PathVariable Long id
    ) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}