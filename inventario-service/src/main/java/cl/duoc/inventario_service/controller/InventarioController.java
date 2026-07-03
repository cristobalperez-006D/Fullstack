package cl.duoc.inventario_service.controller;

import cl.duoc.inventario_service.dto.InventarioDTO;
import cl.duoc.inventario_service.dto.ApiResponseDTO; // Importa tu DTO de respuesta
import cl.duoc.inventario_service.service.InventarioService;
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
@RequestMapping("/api/v1/inventario")
@Tag(name = "Gestión de Inventario y Stock", description = "Motor de control de activos y existencias.")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Consultar inventario global")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de inventario recuperado correctamente.")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<InventarioDTO>>> obtenerTodos() {
        List<InventarioDTO> data = inventarioService.findAll();
        String mensaje = "Matriz de inventario extraída con éxito.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Recuperar disponibilidad por ID de producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock consultado con éxito."),
            @ApiResponse(responseCode = "404", description = "Producto inexistente.")
    })
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ApiResponseDTO<InventarioDTO>> obtenerPorProducto(@PathVariable Long productoId) {
        InventarioDTO data = inventarioService.findByProductoId(productoId);
        String mensaje = "Disponibilidad del producto recuperada.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Registrar nuevo ítem en inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro creado satisfactoriamente.")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<InventarioDTO>> crear(@RequestBody InventarioDTO dto) {
        InventarioDTO data = inventarioService.save(dto);
        String mensaje = "Registro de inventario inicializado correctamente.";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), mensaje, data));
    }

    @Operation(summary = "Reducción transaccional de stock")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock descontado exitosamente."),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente.")
    })
    @PutMapping("/restar/{productoId}")
    public ResponseEntity<ApiResponseDTO<InventarioDTO>> restarStock(
            @PathVariable Long productoId,
            @RequestParam Integer cantidad) {
        InventarioDTO data = inventarioService.restarStock(productoId, cantidad);
        String mensaje = "Operación de descuento ejecutada sin anomalías.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Purga de registros de inventario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro eliminado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminar(@PathVariable Long id) {
        inventarioService.delete(id);
        String mensaje = "El registro ha sido erradicado del sistema.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, null));
    }
}