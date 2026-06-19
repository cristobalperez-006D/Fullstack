package cl.duoc.carrito_service.controller;

import cl.duoc.carrito_service.dto.CarritoDTO;
import cl.duoc.carrito_service.service.CarritoService;
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
@RequestMapping("/api/carrito")
@Tag(name = "Orquestación del Carrito", description = "Motor transaccional de alta concurrencia para la gestión temporal de intenciones de compra. Administra el ciclo de vida de los productos pre-checkout con validación de integridad referencial.")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Operation(
            summary = "Extraer la matriz global de carritos activos",
            description = "Escanea el ecosistema y retorna un snapshot completo de todas las sesiones de compra en memoria. Endpoint estratégico para monitoreo de embudos de conversión."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Extracción del estado global ejecutada sin anomalías.")
    })
    @GetMapping
    public ResponseEntity<List<CarritoDTO>> obtenerTodos() {
        return ResponseEntity.ok(carritoService.obtenerTodos());
    }

    @Operation(
            summary = "Anexar SKU al flujo de compra",
            description = "Inyecta un nuevo producto a la canasta temporal del consumidor. Este proceso valida la estructura del payload y reserva lógicamente el espacio para la futura transacción."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "El ítem fue acoplado al carrito con éxito."),
            @ApiResponse(responseCode = "400", description = "Estructura del DTO malformada o datos inconsistentes.")
    })
    @PostMapping("/agregar")
    public ResponseEntity<CarritoDTO> agregarItem(
            @Parameter(description = "Objeto de transferencia con los metadatos del producto a incorporar")
            @RequestBody CarritoDTO carritoDTO
    ) {
        CarritoDTO response = carritoService.agregarItem(carritoDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Rescatar sesión de compra por identidad de cliente",
            description = "Realiza un query de alta precisión para aislar y compilar el estado exacto del carrito asociado a la huella de un consumidor específico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canasta del usuario localizada y deserializada correctamente.")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CarritoDTO>> obtenerCarrito(
            @Parameter(description = "ID único del consumidor en el sistema central", example = "88")
            @PathVariable Long clienteId
    ) {
        return ResponseEntity.ok(carritoService.obtenerCarritoPorCliente(clienteId));
    }

    @Operation(
            summary = "Expurgar ítem específico de la canasta",
            description = "Ejecuta una remoción quirúrgica de un elemento particular dentro de la orden temporal, recalculando los estados adyacentes del carrito."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "El elemento fue erradicado del carrito exitosamente.")
    })
    @DeleteMapping("/eliminar/{itemId}")
    public ResponseEntity<Void> eliminarItem(
            @Parameter(description = "Identificador interno del ítem a remover", example = "501")
            @PathVariable Long itemId
    ) {
        carritoService.eliminarItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Aniquilación total de la sesión de compra (Wipe-out)",
            description = "Gatilla un proceso de limpieza profunda (flush) que purga absolutamente todos los registros temporales asociados al carrito de un cliente, restaurando su estado a cero."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "La purga del carrito se completó de manera íntegra.")
    })
    @DeleteMapping("/vaciar/{clienteId}")
    public ResponseEntity<Void> vaciarCarrito(
            @Parameter(description = "ID del cliente cuyo ecosistema de compra será reiniciado", example = "88")
            @PathVariable Long clienteId
    ) {
        carritoService.vaciarCarrito(clienteId);
        return ResponseEntity.noContent().build();
    }
}