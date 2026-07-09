package cl.duoc.notificaciones_service.controller;

import cl.duoc.notificaciones_service.dto.NotificacionDTO;
import cl.duoc.notificaciones_service.dto.ApiResponseDTO; // ¡No olvides importar esto!
import cl.duoc.notificaciones_service.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@Tag(name = "Notificaciones", description = "Servicio de mensajería y eventos")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Operation(summary = "Recuperar bitácora global")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<NotificacionDTO>>> obtenerTodas() {
        List<NotificacionDTO> data = notificacionService.findAll();
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Historial extraído", data));
    }

    @Operation(summary = "Obtener por cliente")
    @ApiResponse(responseCode = "200", description = "Notificaciones recuperadas")
    @GetMapping(value = "/cliente/{clienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<NotificacionDTO>>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<NotificacionDTO> data = notificacionService.findByClienteId(clienteId);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Eventos compilados", data));
    }

    @Operation(summary = "Gatillar nueva notificación")
    @ApiResponse(responseCode = "201", description = "Notificación creada")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<NotificacionDTO>> crear(@Valid @RequestBody NotificacionDTO dto) {
        NotificacionDTO data = notificacionService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), "Alerta persistida", data));
    }

    @Operation(summary = "Depuración de alertas")
    @ApiResponse(responseCode = "204", description = "Alerta eliminada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Contar notificaciones de un cliente")
    @ApiResponse(responseCode = "200", description = "Conteo realizado")
    @GetMapping("/count/{clienteId}")
    public ResponseEntity<ApiResponseDTO<Long>> contarPorCliente(@PathVariable Long clienteId) {
        Long total = notificacionService.contarPorCliente(clienteId);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Conteo exitoso", total));
    }

    @Operation(summary = "Contar notificaciones por tipo")
    @ApiResponse(responseCode = "200", description = "Conteo por tipo exitoso")
    @GetMapping("/count-tipo/{tipo}")
    public ResponseEntity<ApiResponseDTO<Long>> contarPorTipo(@PathVariable String tipo) {
        Long total = notificacionService.contarPorTipo(tipo);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Conteo por tipo exitoso", total));
    }

    @Operation(summary = "Limpiar notificaciones antiguas (30 días)")
    @ApiResponse(responseCode = "204", description = "Limpieza exitosa")
    @DeleteMapping("/limpiar/{clienteId}")
    public ResponseEntity<Void> limpiarAntiguas(@PathVariable Long clienteId) {
        notificacionService.limpiarNotificacionesAntiguas(clienteId);
        return ResponseEntity.noContent().build();
    }
}