package cl.duoc.envios_service.controller;

import cl.duoc.envios_service.dto.EnvioDTO;
import cl.duoc.envios_service.dto.ApiResponseDTO; // ¡No olvides este import!
import cl.duoc.envios_service.service.EnvioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/envios")
@Tag(name = "Gestión de Logística y Envíos", description = "Servicio core para la trazabilidad y monitoreo del flujo logístico.")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @Operation(summary = "Recuperar el catálogo logístico completo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de envíos recuperado exitosamente.")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<EnvioDTO>>> obtenerTodos() {
        List<EnvioDTO> data = envioService.findAll();
        String mensaje = "Catálogo logístico extraído correctamente.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Localizar envío por Identificador Único")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalles del envío encontrados."),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EnvioDTO>> obtenerPorId(@PathVariable Long id) {
        EnvioDTO data = envioService.findById(id);
        String mensaje = "Detalles técnicos del despacho recuperados.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Recuperar trazabilidad asociada a un pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trazabilidad encontrada."),
            @ApiResponse(responseCode = "404", description = "No existe envío asociado.")
    })
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<ApiResponseDTO<EnvioDTO>> obtenerPorPedido(@PathVariable Long pedidoId) {
        EnvioDTO data = envioService.findByPedidoId(pedidoId);
        String mensaje = "Trazabilidad del pedido compilada con éxito.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Registrar nuevo proceso de despacho")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Despacho inicializado.")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<EnvioDTO>> crear(@RequestBody EnvioDTO dto) {
        EnvioDTO data = envioService.save(dto);
        String mensaje = "Ciclo de vida logístico iniciado para el pedido.";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), mensaje, data));
    }

    @Operation(summary = "Actualizar estado operativo del envío")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado sincronizado.")
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponseDTO<EnvioDTO>> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        EnvioDTO data = envioService.updateEstado(id, estado);
        String mensaje = "Transición de estado logístico ejecutada exitosamente.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Eliminación de registros logísticos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro erradicado con éxito.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminar(@PathVariable Long id) {
        envioService.delete(id);
        String mensaje = "El registro logístico fue purgado del sistema.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, null));
    }
}