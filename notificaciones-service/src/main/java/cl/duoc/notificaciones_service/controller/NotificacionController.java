package cl.duoc.notificaciones_service.controller;

import cl.duoc.notificaciones_service.dto.NotificacionDTO;
import cl.duoc.notificaciones_service.dto.ApiResponseDTO; // ¡No olvides importar esto!
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
@RequestMapping("/api/v1/notificaciones")
@Tag(name = "Gestión de Notificaciones y Alertas", description = "Servicio de mensajería y eventos encargado de la comunicación asíncrona.")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Operation(summary = "Recuperar bitácora global de notificaciones")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bitácora recuperada con éxito.")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<NotificacionDTO>>> obtenerTodas() {
        List<NotificacionDTO> data = notificacionService.findAll();
        String mensaje = "Historial completo de notificaciones extraído.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Obtener notificaciones por perfil de usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificaciones recuperadas."),
            @ApiResponse(responseCode = "404", description = "No existen alertas para este usuario.")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponseDTO<List<NotificacionDTO>>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<NotificacionDTO> data = notificacionService.findByClienteId(clienteId);
        String mensaje = "Eventos del cliente compilados correctamente.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Gatillar registro de nueva notificación")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notificación integrada al pipeline.")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<NotificacionDTO>> crear(@RequestBody NotificacionDTO dto) {
        NotificacionDTO data = notificacionService.save(dto);
        String mensaje = "Alerta persistida y lista para ser despachada.";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), mensaje, data));
    }

    @Operation(summary = "Depuración de alertas obsoletas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alerta erradicada.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminar(@PathVariable Long id) {
        notificacionService.delete(id);
        String mensaje = "El registro de la alerta fue purgado del sistema.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, null));
    }
}