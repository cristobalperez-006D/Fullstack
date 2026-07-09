package cl.duoc.envios_service.controller;

import cl.duoc.envios_service.dto.EnvioDTO;
import cl.duoc.envios_service.dto.ApiResponseDTO; // ¡No olvides este import!
import cl.duoc.envios_service.service.EnvioService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/envios")
@Tag(name = "Envíos", description = "Gestión logística y trazabilidad")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @Operation(summary = "Recuperar el catálogo logístico")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<EnvioDTO>>> obtenerTodos() {
        List<EnvioDTO> data = envioService.findAll();
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Catálogo extraído con éxito", data));
    }

    @Operation(summary = "Localizar envío por ID")
    @ApiResponse(responseCode = "200", description = "Envío encontrado")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<EnvioDTO>> obtenerPorId(@PathVariable Long id) {
        EnvioDTO data = envioService.findById(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Detalles recuperados", data));
    }

    @Operation(summary = "Trazabilidad por pedido")
    @ApiResponse(responseCode = "200", description = "Trazabilidad encontrada")
    @GetMapping(value = "/pedido/{pedidoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<EnvioDTO>> obtenerPorPedido(@PathVariable Long pedidoId) {
        EnvioDTO data = envioService.findByPedidoId(pedidoId);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Trazabilidad localizada", data));
    }

    @Operation(summary = "Registrar nuevo despacho")
    @ApiResponse(responseCode = "201", description = "Despacho inicializado")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<EnvioDTO>> crear(@Valid @RequestBody EnvioDTO dto) {
        EnvioDTO data = envioService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), "Ciclo logístico iniciado", data));
    }

    @Operation(summary = "Actualizar estado logístico")
    @ApiResponse(responseCode = "200", description = "Estado sincronizado")
    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponseDTO<EnvioDTO>> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        EnvioDTO data = envioService.updateEstado(id, estado);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Estado actualizado", data));
    }

    @Operation(summary = "Purgar registro logístico")
    @ApiResponse(responseCode = "204", description = "Registro eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        envioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Contar envíos por estado (ej: PENDIENTE, ENVIADO)")
    @ApiResponse(responseCode = "200", description = "Conteo realizado")
    @GetMapping(value = "/count-estado/{estado}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<Long>> contarPorEstado(@PathVariable String estado) {
        Long total = envioService.contarPorEstado(estado);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Conteo exitoso", total));
    }
}