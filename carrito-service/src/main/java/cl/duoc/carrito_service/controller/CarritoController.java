package cl.duoc.carrito_service.controller;

import cl.duoc.carrito_service.dto.CarritoDTO;
import cl.duoc.carrito_service.dto.ApiResponseDTO; // Asegúrate de importar esto
import cl.duoc.carrito_service.service.CarritoService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/carrito")
@Tag(name = "Carrito", description = "Gestión de la canasta de compras")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Operation(summary = "Listar todos los carritos")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<CarritoDTO>>> obtenerTodos() {
        List<CarritoDTO> data = carritoService.obtenerTodos();
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Lista extraída correctamente", data));
    }

    @Operation(summary = "Agregar ítem al carrito")
    @ApiResponse(responseCode = "201", description = "Ítem agregado con éxito")
    @PostMapping(value = "/agregar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<CarritoDTO>> agregarItem(@Valid @RequestBody CarritoDTO carritoDTO) {
        CarritoDTO data = carritoService.agregarItem(carritoDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), "Ítem acoplado con éxito", data));
    }

    @Operation(summary = "Obtener carrito por cliente")
    @ApiResponse(responseCode = "200", description = "Carrito encontrado")
    @GetMapping(value = "/cliente/{clienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<CarritoDTO>>> obtenerCarrito(@PathVariable Long clienteId) {
        List<CarritoDTO> data = carritoService.obtenerCarritoPorCliente(clienteId);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Canasta localizada con éxito", data));
    }

    @Operation(summary = "Eliminar ítem")
    @ApiResponse(responseCode = "204", description = "Ítem eliminado correctamente")
    @DeleteMapping("/eliminar/{itemId}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long itemId) {
        carritoService.eliminarItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Vaciar carrito")
    @ApiResponse(responseCode = "204", description = "Carrito vaciado")
    @DeleteMapping("/vaciar/{clienteId}")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable Long clienteId) {
        carritoService.vaciarCarrito(clienteId);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Verificar si un producto ya está en el carrito")
    @ApiResponse(responseCode = "200", description = "Resultado de la verificación")
    @GetMapping("/existe/{clienteId}/{productoId}")
    public ResponseEntity<ApiResponseDTO<Boolean>> existeEnCarrito(
            @PathVariable Long clienteId,
            @PathVariable Long productoId) {
        boolean existe = carritoService.existeEnCarrito(clienteId, productoId);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Verificación realizada", existe));
    }

    @Operation(summary = "Calcular total a pagar")
    @ApiResponse(responseCode = "200", description = "Total calculado")
    @GetMapping("/total/{clienteId}")
    public ResponseEntity<ApiResponseDTO<BigDecimal>> calcularTotal(@PathVariable Long clienteId) {
        BigDecimal total = carritoService.calcularTotalCarrito(clienteId);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Total calculado", total));
    }
}