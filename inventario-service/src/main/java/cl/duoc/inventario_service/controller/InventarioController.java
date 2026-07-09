package cl.duoc.inventario_service.controller;

import cl.duoc.inventario_service.dto.InventarioDTO;
import cl.duoc.inventario_service.dto.ApiResponseDTO; // Importa tu DTO de respuesta
import cl.duoc.inventario_service.service.InventarioService;
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
@RequestMapping("/api/v1/inventario")
@Tag(name = "Inventario", description = "Gestión de activos y existencias")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Consultar inventario global")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<InventarioDTO>>> obtenerTodos() {
        List<InventarioDTO> data = inventarioService.findAll();
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Inventario extraído", data));
    }

    @Operation(summary = "Recuperar disponibilidad por producto")
    @ApiResponse(responseCode = "200", description = "Stock encontrado")
    @GetMapping(value = "/producto/{productoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<InventarioDTO>> obtenerPorProducto(@PathVariable Long productoId) {
        InventarioDTO data = inventarioService.findByProductoId(productoId);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Disponibilidad recuperada", data));
    }

    @Operation(summary = "Registrar nuevo ítem")
    @ApiResponse(responseCode = "201", description = "Registro creado")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<InventarioDTO>> crear(@Valid @RequestBody InventarioDTO dto) {
        InventarioDTO data = inventarioService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), "Registro inicializado", data));
    }

    @Operation(summary = "Reducción transaccional de stock")
    @ApiResponse(responseCode = "200", description = "Stock descontado")
    @PutMapping("/restar/{productoId}")
    public ResponseEntity<ApiResponseDTO<InventarioDTO>> restarStock(
            @PathVariable Long productoId,
            @RequestParam Integer cantidad) {
        InventarioDTO data = inventarioService.restarStock(productoId, cantidad);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Descuento ejecutado", data));
    }

    @Operation(summary = "Purga de registros")
    @ApiResponse(responseCode = "204", description = "Registro eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reabastecimiento de existencias")
    @ApiResponse(responseCode = "200", description = "Stock incrementado")
    @PutMapping("/sumar/{productoId}")
    public ResponseEntity<ApiResponseDTO<InventarioDTO>> sumarStock(
            @PathVariable Long productoId,
            @RequestParam Integer cantidad) {
        InventarioDTO data = inventarioService.sumarStock(productoId, cantidad);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Stock sumado exitosamente", data));
    }

    @Operation(summary = "Consultar alerta de stock bajo")
    @ApiResponse(responseCode = "200", description = "Estado de alerta retornado")
    @GetMapping("/alerta/{productoId}")
    public ResponseEntity<ApiResponseDTO<Boolean>> esStockCritico(@PathVariable Long productoId) {
        boolean esCritico = inventarioService.tieneStockCritico(productoId);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Consulta de alerta", esCritico));
    }
}