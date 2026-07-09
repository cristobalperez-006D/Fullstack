package cl.duoc.pedidos_service.controller;

import cl.duoc.pedidos_service.dto.PedidoDTO;
import cl.duoc.pedidos_service.dto.ApiResponseDTO; // Importa tu DTO de respuesta
import cl.duoc.pedidos_service.service.PedidoService;
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
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "Gestión integral del ciclo de vida de las órdenes")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Operation(summary = "Recuperar el catálogo histórico de pedidos")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<PedidoDTO>>> obtenerTodos() {
        List<PedidoDTO> data = pedidoService.findAll();
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Catálogo extraído con éxito", data));
    }

    @Operation(summary = "Inspeccionar un pedido específico")
    @ApiResponse(responseCode = "200", description = "Pedido localizado")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<PedidoDTO>> obtenerPorId(@PathVariable Long id) {
        PedidoDTO data = pedidoService.findById(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Detalles recuperados", data));
    }

    @Operation(summary = "Listar el historial de un cliente")
    @ApiResponse(responseCode = "200", description = "Historial recuperado")
    @GetMapping(value = "/cliente/{clienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<PedidoDTO>>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<PedidoDTO> data = pedidoService.findByClienteId(clienteId);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Historial localizado", data));
    }

    @Operation(summary = "Registrar una nueva orden")
    @ApiResponse(responseCode = "201", description = "Pedido creado con éxito")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<PedidoDTO>> crear(@Valid @RequestBody PedidoDTO dto) {
        PedidoDTO data = pedidoService.crearPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), "Pedido formalizado", data));
    }

    @Operation(summary = "Transicionar estado logístico")
    @ApiResponse(responseCode = "200", description = "Estado sincronizado")
    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponseDTO<PedidoDTO>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        PedidoDTO data = pedidoService.actualizarEstado(id, estado);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Trazabilidad actualizada", data));
    }

    @Operation(summary = "Purgar un pedido del sistema")
    @ApiResponse(responseCode = "204", description = "Registro eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Calcular total gastado por cliente")
    @GetMapping("/total-gastado/{clienteId}")
    public ResponseEntity<ApiResponseDTO<java.math.BigDecimal>> calcularTotal(@PathVariable Long clienteId) {
        java.math.BigDecimal total = pedidoService.calcularTotalGastadoPorCliente(clienteId);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Cálculo exitoso", total));
    }
}