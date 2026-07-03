package cl.duoc.carrito_service.controller;

import cl.duoc.carrito_service.dto.CarritoDTO;
import cl.duoc.carrito_service.dto.ApiResponseDTO; // Asegúrate de importar esto
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
@RequestMapping("/api/v1/carrito")
@Tag(name = "Orquestación del Carrito", description = "Motor transaccional de alta concurrencia para la gestión temporal de intenciones de compra.")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Operation(summary = "Extraer la matriz global de carritos activos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Extracción del estado global ejecutada sin anomalías.")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CarritoDTO>>> obtenerTodos() {
        List<CarritoDTO> data = carritoService.obtenerTodos();
        String mensaje = "Matriz global de carritos extraída correctamente.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Anexar SKU al flujo de compra")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "El ítem fue acoplado al carrito con éxito."),
            @ApiResponse(responseCode = "400", description = "Datos inconsistentes.")
    })
    @PostMapping("/agregar")
    public ResponseEntity<ApiResponseDTO<CarritoDTO>> agregarItem(@RequestBody CarritoDTO carritoDTO) {
        CarritoDTO data = carritoService.agregarItem(carritoDTO);
        String mensaje = "¡Ítem acoplado con éxito al ecosistema de compra!";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), mensaje, data));
    }

    @Operation(summary = "Rescatar sesión de compra por identidad de cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canasta del usuario localizada.")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponseDTO<List<CarritoDTO>>> obtenerCarrito(@PathVariable Long clienteId) {
        List<CarritoDTO> data = carritoService.obtenerCarritoPorCliente(clienteId);
        String mensaje = "Canasta del cliente localizada con éxito.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Expurgar ítem específico de la canasta")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "El elemento fue erradicado del carrito exitosamente.")
    })
    @DeleteMapping("/eliminar/{itemId}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminarItem(@PathVariable Long itemId) {
        carritoService.eliminarItem(itemId);
        String mensaje = "Elemento eliminado de forma quirúrgica.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, null));
    }

    @Operation(summary = "Aniquilación total de la sesión de compra")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "La purga del carrito se completó.")
    })
    @DeleteMapping("/vaciar/{clienteId}")
    public ResponseEntity<ApiResponseDTO<Void>> vaciarCarrito(@PathVariable Long clienteId) {
        carritoService.vaciarCarrito(clienteId);
        String mensaje = "Ecosistema de compra reiniciado a cero.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, null));
    }
}