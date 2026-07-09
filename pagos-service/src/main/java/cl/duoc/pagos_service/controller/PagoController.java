package cl.duoc.pagos_service.controller;

import cl.duoc.pagos_service.dto.PagoDTO;
import cl.duoc.pagos_service.dto.ApiResponseDTO; // ¡Importa tu DTO de respuesta!
import cl.duoc.pagos_service.service.PagoService;
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
@RequestMapping("/api/v1/pagos")
@Tag(name = "Pagos", description = "Servicio transaccional de pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Operation(summary = "Obtener bitácora de transacciones")
    @ApiResponse(responseCode = "200", description = "Listado de transacciones recuperado")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<PagoDTO>>> obtenerTodos() {
        List<PagoDTO> data = pagoService.findAll();
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Historial recuperado correctamente", data));
    }

    @Operation(summary = "Consultar detalle de pago por ID")
    @ApiResponse(responseCode = "200", description = "Detalle recuperado")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<PagoDTO>> obtenerPorId(@PathVariable Long id) {
        PagoDTO data = pagoService.findById(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Detalle recuperado", data));
    }

    @Operation(summary = "Procesar nuevo flujo de pago")
    @ApiResponse(responseCode = "201", description = "Transacción procesada con éxito")
    @PostMapping(value = "/procesar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<PagoDTO>> procesarPago(@Valid @RequestBody PagoDTO dto) {
        PagoDTO data = pagoService.registrarPago(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), "Pago registrado exitosamente", data));
    }

    @Operation(summary = "Obtener pagos de un pedido")
    @GetMapping(value = "/pedido/{pedidoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<PagoDTO>>> obtenerPorPedido(@PathVariable Long pedidoId) {
        List<PagoDTO> data = pagoService.findByPedidoId(pedidoId);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Pagos del pedido recuperados", data));
    }

    @Operation(summary = "Obtener pagos de un cliente")
    @GetMapping(value = "/cliente/{clienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<PagoDTO>>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<PagoDTO> data = pagoService.findByClienteId(clienteId);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Pagos del cliente recuperados", data));
    }

    @Operation(summary = "Calcular total pagado por cliente")
    @GetMapping(value = "/total-cliente/{clienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<java.math.BigDecimal>> calcularTotal(@PathVariable Long clienteId) {
        java.math.BigDecimal total = pagoService.calcularTotalPagadoPorCliente(clienteId);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Cálculo realizado", total));
    }

    @Operation(summary = "Eliminar registro de pago")
    @ApiResponse(responseCode = "204", description = "Registro eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id) {
        pagoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}