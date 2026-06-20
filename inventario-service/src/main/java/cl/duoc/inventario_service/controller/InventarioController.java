package cl.duoc.inventario_service.controller;
import cl.duoc.inventario_service.dto.InventarioDTO;
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
@Tag(name = "Gestión de Inventario y Stock", description = "Motor de control de activos y existencias. Administra los niveles de stock en tiempo real y valida la disponibilidad de productos para garantizar la integridad en el proceso de checkout.")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(
            summary = "Consultar inventario global",
            description = "Extrae el estado actual de todas las existencias registradas en bodega. Herramienta fundamental para el análisis de disponibilidad de productos a nivel corporativo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de inventario recuperado correctamente.")
    })
    @GetMapping
    public ResponseEntity<List<InventarioDTO>> obtenerTodos() {
        return ResponseEntity.ok(inventarioService.findAll());
    }

    @Operation(
            summary = "Recuperar disponibilidad por ID de producto",
            description = "Realiza un query de alta precisión para verificar el stock actual de un producto específico, facilitando la toma de decisiones en el carrito de compras."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock del producto consultado con éxito."),
            @ApiResponse(responseCode = "404", description = "El producto no posee registros en el sistema de inventarios.")
    })
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<InventarioDTO> obtenerPorProducto(
            @Parameter(description = "ID del producto a consultar", example = "101")
            @PathVariable Long productoId
    ) {
        InventarioDTO inv = inventarioService.findByProductoId(productoId);
        return inv != null ? ResponseEntity.ok(inv) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Registrar nuevo ítem en inventario",
            description = "Persiste un nuevo registro de existencias en el catálogo de inventario, inicializando los niveles de stock para un SKU específico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro de inventario creado satisfactoriamente."),
            @ApiResponse(responseCode = "400", description = "Error en el payload de creación.")
    })
    @PostMapping
    public ResponseEntity<InventarioDTO> crear(
            @Parameter(description = "DTO con la metadata de existencias")
            @RequestBody InventarioDTO dto
    ) {
        InventarioDTO creado = inventarioService.save(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Reducción transaccional de stock",
            description = "Ejecuta una operación crítica para descontar unidades del stock disponible ante la confirmación de una orden. Incluye validaciones de integridad para evitar saldos negativos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock descontado exitosamente tras la venta."),
            @ApiResponse(responseCode = "400", description = "Operación abortada: stock insuficiente o parámetros inválidos.")
    })
    @PutMapping("/restar/{productoId}")
    public ResponseEntity<InventarioDTO> restarStock(
            @Parameter(description = "ID del producto a actualizar", example = "101")
            @PathVariable Long productoId,
            @Parameter(description = "Cantidad de unidades a descontar del stock", example = "1")
            @RequestParam Integer cantidad
    ) {
        InventarioDTO actualizado = inventarioService.restarStock(productoId, cantidad);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.badRequest().build();
    }

    @Operation(
            summary = "Purga de registros de inventario",
            description = "Elimina un registro de control de stock de la base de datos. Operación administrativa para la gestión de productos fuera de catálogo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Registro de inventario eliminado correctamente.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del registro de inventario a eliminar", example = "50")
            @PathVariable Long id
    ) {
        inventarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}