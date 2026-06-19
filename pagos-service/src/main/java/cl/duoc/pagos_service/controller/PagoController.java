package cl.duoc.pagos_service.controller;

import cl.duoc.pagos_service.dto.PagoDTO;
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
@RequestMapping("/api/pagos")
@Tag(name = "Orquestación de Pasarela de Pagos", description = "Servicio transaccional de alta seguridad encargado de la validación, ejecución y auditoría de los pagos electrónicos realizados dentro de la plataforma.")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Operation(
            summary = "Obtener bitácora de transacciones",
            description = "Extrae el historial completo de todos los movimientos financieros procesados por el sistema. Endpoint optimizado para conciliación bancaria y auditorías financieras."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de transacciones extraído con éxito.")
    })
    @GetMapping
    public ResponseEntity<List<PagoDTO>> obtenerTodos() {
        return ResponseEntity.ok(pagoService.findAll());
    }

    @Operation(
            summary = "Consultar detalle de un pago específico",
            description = "Recupera la metadata técnica y de estado de una transacción individual mediante su identificador único, garantizando la trazabilidad de los flujos de dinero."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle del pago recuperado."),
            @ApiResponse(responseCode = "404", description = "La transacción no existe en los registros contables.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> obtenerPorId(
            @Parameter(description = "Identificador único de la transacción", example = "550")
            @PathVariable Long id
    ) {
        PagoDTO pago = pagoService.findById(id);
        return pago != null ? ResponseEntity.ok(pago) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Ejecutar flujo de pago transaccional",
            description = "Gatilla el proceso de procesamiento de pagos contra la pasarela financiera externa. Realiza validaciones de integridad de datos y confirma el éxito de la operación monetaria."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transacción financiera procesada y registrada en el sistema."),
            @ApiResponse(responseCode = "400", description = "Fallido: datos de pago inválidos o error en la pasarela.")
    })
    @PostMapping("/procesar")
    public ResponseEntity<PagoDTO> procesarPago(
            @Parameter(description = "Payload con los datos para la ejecución del pago")
            @RequestBody PagoDTO dto
    ) {
        PagoDTO creado = pagoService.registrarPago(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }
}