package cl.duoc.producto_service.controller;

import cl.duoc.producto_service.dto.ProductoDTO;
import cl.duoc.producto_service.dto.ApiResponseDTO; // Importa tu DTO de respuesta
import cl.duoc.producto_service.service.ProductoService;
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
@RequestMapping("/api/v1/productos")
@Tag(name = "Catálogo de Productos", description = "API dedicada a la gestión integral del inventario.")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(summary = "Listado maestro de productos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catálogo recuperado exitosamente.")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ProductoDTO>>> obtenerTodos() {
        List<ProductoDTO> data = productoService.findAll();
        String mensaje = "Catálogo maestro de productos extraído.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Recuperar detalles técnicos del producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto localizado."),
            @ApiResponse(responseCode = "404", description = "Producto inexistente.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductoDTO>> obtenerProductoPorId(@PathVariable Long id) {
        ProductoDTO data = productoService.findById(id);
        String mensaje = "Detalles técnicos del producto recuperados con precisión.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Aprovisionar nuevo producto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto registrado e integrado.")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ProductoDTO>> crear(@RequestBody ProductoDTO dto) {
        ProductoDTO data = productoService.save(dto);
        String mensaje = "Nuevo SKU integrado al inventario central.";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), mensaje, data));
    }

    @Operation(summary = "Actualizar atributos del producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductoDTO>> actualizar(@PathVariable Long id, @RequestBody ProductoDTO dto) {
        ProductoDTO data = productoService.update(id, dto);
        String mensaje = "Atributos del producto sincronizados con éxito.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Descontinuar/Eliminar producto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto eliminado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminar(@PathVariable Long id) {
        productoService.delete(id);
        String mensaje = "Producto erradicado del catálogo.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, null));
    }
}