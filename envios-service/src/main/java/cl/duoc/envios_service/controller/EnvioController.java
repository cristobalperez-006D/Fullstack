package cl.duoc.envios_service.controller;
import cl.duoc.envios_service.dto.EnvioDTO;
import cl.duoc.envios_service.service.EnvioService;
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
@RequestMapping("/api/envios")
@Tag(name = "Gestión de Logística y Envíos", description = "Servicio core para la trazabilidad y monitoreo del flujo logístico de los pedidos. Gestiona las transiciones de estado de despacho y asegura la integridad del seguimiento de paquetes en el ecosistema.")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @Operation(
            summary = "Recuperar el catálogo logístico completo",
            description = "Consulta la base de datos para obtener el listado histórico y actual de todos los envíos gestionados por el sistema. Vital para reportabilidad operativa y auditorías de despacho."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de envíos recuperado exitosamente.")
    })
    @GetMapping
    public ResponseEntity<List<EnvioDTO>> obtenerTodos() {
        return ResponseEntity.ok(envioService.findAll());
    }

    @Operation(
            summary = "Localizar envío por Identificador Único",
            description = "Ejecuta una búsqueda directa para extraer los detalles técnicos y de estado de un despacho específico, garantizando visibilidad inmediata sobre la ubicación del paquete."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalles del envío encontrados correctamente."),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado en el sistema.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EnvioDTO> obtenerPorId(
            @Parameter(description = "ID único del despacho", example = "777")
            @PathVariable Long id
    ) {
        EnvioDTO envio = envioService.findById(id);
        return envio != null ? ResponseEntity.ok(envio) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Recuperar trazabilidad asociada a un pedido",
            description = "Asocia y extrae el estado logístico ligado a una orden de compra específica. Permite al cliente conocer el estatus real de su pedido final."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trazabilidad encontrada."),
            @ApiResponse(responseCode = "404", description = "No existe un envío asociado al pedido indicado.")
    })
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<EnvioDTO> obtenerPorPedido(
            @Parameter(description = "ID del pedido a consultar", example = "1050")
            @PathVariable Long pedidoId
    ) {
        EnvioDTO envio = envioService.findByPedidoId(pedidoId);
        return envio != null ? ResponseEntity.ok(envio) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Registrar nuevo proceso de despacho",
            description = "Inicia el ciclo de vida logístico para un nuevo pedido, persistiendo los datos de envío en el sistema y gatillando la configuración de seguimiento."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Despacho creado y correctamente inicializado."),
            @ApiResponse(responseCode = "400", description = "Datos de envío inconsistentes.")
    })
    @PostMapping
    public ResponseEntity<EnvioDTO> crear(
            @Parameter(description = "Objeto con la metadata del nuevo envío")
            @RequestBody EnvioDTO dto
    ) {
        EnvioDTO creado = envioService.save(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Actualizar estado operativo del envío",
            description = "Realiza una transición de estado en tiempo real (ej: 'EN_RUTA', 'ENTREGADO'). Endpoint crítico para la sincronización con los sistemas de transportistas externos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado del envío actualizado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado.")
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<EnvioDTO> actualizarEstado(
            @Parameter(description = "ID del envío a modificar", example = "777")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado logístico", example = "EN_DESTINO")
            @RequestParam String estado
    ) {
        EnvioDTO actualizado = envioService.updateEstado(id, estado);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Eliminación de registros logísticos",
            description = "Purgar un registro de envío del sistema. Operación restringida para casos de excepción o limpieza de anomalías de datos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Registro erradicado con éxito.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del envío a eliminar", example = "777")
            @PathVariable Long id
    ) {
        envioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}