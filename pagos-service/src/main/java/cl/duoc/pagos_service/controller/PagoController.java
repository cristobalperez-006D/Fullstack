package cl.duoc.pagos_service.controller;

import cl.duoc.pagos_service.dto.PagoDTO;
import cl.duoc.pagos_service.dto.ApiResponseDTO; // ¡Importa tu DTO de respuesta!
import cl.duoc.pagos_service.service.PagoService;
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
@RequestMapping("/api/v1/pagos")
@Tag(name = "Orquestación de Pasarela de Pagos", description = "Servicio transaccional de alta seguridad.")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Operation(summary = "Obtener bitácora de transacciones")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de transacciones extraído.")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PagoDTO>>> obtenerTodos() {
        List<PagoDTO> data = pagoService.findAll();
        String mensaje = "Historial completo de transacciones recuperado.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Consultar detalle de un pago específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle recuperado."),
            @ApiResponse(responseCode = "404", description = "Transacción inexistente.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PagoDTO>> obtenerPorId(@PathVariable Long id) {
        PagoDTO data = pagoService.findById(id);
        String mensaje = "Detalle técnico de la transacción recuperado.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Ejecutar flujo de pago transaccional")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transacción procesada exitosamente.")
    })
    @PostMapping("/procesar")
    public ResponseEntity<ApiResponseDTO<PagoDTO>> procesarPago(@RequestBody PagoDTO dto) {
        PagoDTO data = pagoService.registrarPago(dto);
        String mensaje = "¡Operación monetaria procesada y registrada en el ecosistema!";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), mensaje, data));
    }
}