package cl.duoc.notificaciones_service.controller;

import cl.duoc.notificaciones_service.dto.NotificacionDTO;
import cl.duoc.notificaciones_service.service.NotificacionService;
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
@RequestMapping("/api/notificaciones")
@Tag(name = "Gestión de Notificaciones y Alertas", description = "Servicio de mensajería y eventos encargado de la comunicación asíncrona hacia los usuarios finales. Gestiona el registro y visualización de alertas críticas del sistema.")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Operation(
            summary = "Recuperar bitácora global de notificaciones",
            description = "Extrae el historial completo de alertas y avisos generados por el sistema. Endpoint de soporte para auditoría operativa y monitoreo de comunicación al usuario."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bitácora de notificaciones recuperada exitosamente.")
    })
    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> obtenerTodas() {
        return ResponseEntity.ok(notificacionService.findAll());
    }

    @Operation(
            summary = "Obtener notificaciones por perfil de usuario",
            description = "Filtra y recupera todas las notificaciones (leídas o pendientes) asociadas a un cliente específico, garantizando que el usuario esté siempre informado de sus eventos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificaciones del cliente recuperadas correctamente.")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<NotificacionDTO>> obtenerPorCliente(
            @Parameter(description = "Identificador único del cliente", example = "42")
            @PathVariable Long clienteId
    ) {
        List<NotificacionDTO> notificaciones = notificacionService.findByClienteId(clienteId);
        return ResponseEntity.ok(notificaciones);
    }

    @Operation(
            summary = "Gatillar registro de nueva notificación",
            description = "Persiste un nuevo evento de notificación en el repositorio, listo para ser despachado al canal de comunicación correspondiente del usuario."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notificación registrada e integrada al pipeline de mensajes."),
            @ApiResponse(responseCode = "400", description = "Error en el payload de notificación.")
    })
    @PostMapping
    public ResponseEntity<NotificacionDTO> crear(
            @Parameter(description = "DTO con los datos técnicos de la notificación")
            @RequestBody NotificacionDTO dto
    ) {
        NotificacionDTO creada = notificacionService.save(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Depuración de alertas obsoletas",
            description = "Ejecuta la remoción física de un registro de notificación del sistema. Operación necesaria para la limpieza de logs antiguos o corrección de eventos fallidos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Registro de notificación erradicado con éxito.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID único de la notificación a purgar", example = "999")
            @PathVariable Long id
    ) {
        notificacionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}