package cl.duoc.pedidos_service.controller;

import cl.duoc.pedidos_service.dto.PedidoDTO;
import cl.duoc.pedidos_service.dto.ApiResponseDTO; // Importa tu DTO de respuesta
import cl.duoc.pedidos_service.service.PedidoService;
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
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Gestión de Pedidos", description = "API centralizada para la orquestación y gestión integral del ciclo de vida de las órdenes.")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Operation(summary = "Recuperar el catálogo histórico de pedidos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Extracción de datos exitosa.")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PedidoDTO>>> obtenerTodos() {
        List<PedidoDTO> data = pedidoService.findAll();
        String mensaje = "Catálogo histórico extraído con éxito.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Inspeccionar un pedido específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido localizado."),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PedidoDTO>> obtenerPorId(@PathVariable Long id) {
        PedidoDTO data = pedidoService.findById(id);
        String mensaje = "Detalles del pedido recuperados correctamente.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Listar el historial de compras de un cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial del cliente recuperado.")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponseDTO<List<PedidoDTO>>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<PedidoDTO> data = pedidoService.findByClienteId(clienteId);
        String mensaje = "Historial de transacciones localizado.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Emitir y registrar una nueva orden")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado con éxito.")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<PedidoDTO>> crear(@RequestBody PedidoDTO dto) {
        PedidoDTO data = pedidoService.crearPedido(dto);
        String mensaje = "¡Pedido formalizado e inyectado en el sistema logístico!";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), mensaje, data));
    }

    @Operation(summary = "Transicionar el estado logístico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado sincronizado.")
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponseDTO<PedidoDTO>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        PedidoDTO data = pedidoService.actualizarEstado(id, estado);
        String mensaje = "Trazabilidad del pedido actualizada con éxito.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Purgar un pedido del sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "El registro fue eliminado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminar(@PathVariable Long id) {
        pedidoService.delete(id);
        String mensaje = "Registro erradicado de la base de datos.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, null));
    }
}