package cl.duoc.producto_service.controller;

import cl.duoc.producto_service.dto.ProductoDTO;
import cl.duoc.producto_service.dto.ApiResponseDTO; // Importa tu DTO de respuesta
import cl.duoc.producto_service.service.ProductoService;
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
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Gestión del catálogo de productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(summary = "Listado maestro de productos")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<ProductoDTO>>> obtenerTodos() {
        List<ProductoDTO> data = productoService.findAll();
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Catálogo extraído", data));
    }

    @Operation(summary = "Recuperar detalles del producto")
    @ApiResponse(responseCode = "200", description = "Producto localizado")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<ProductoDTO>> obtenerProductoPorId(@PathVariable Long id) {
        ProductoDTO data = productoService.findById(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Detalles recuperados", data));
    }

    @Operation(summary = "Aprovisionar nuevo producto")
    @ApiResponse(responseCode = "201", description = "Producto registrado")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<ProductoDTO>> crear(@Valid @RequestBody ProductoDTO dto) {
        ProductoDTO data = productoService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), "SKU integrado", data));
    }

    @Operation(summary = "Actualizar atributos")
    @ApiResponse(responseCode = "200", description = "Producto actualizado")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<ProductoDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        ProductoDTO data = productoService.update(id, dto);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Producto actualizado", data));
    }

    @Operation(summary = "Descontinuar producto")
    @ApiResponse(responseCode = "204", description = "Producto eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar productos por nombre")
    @GetMapping(value = "/search/{nombre}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<ProductoDTO>>> buscarPorNombre(@PathVariable String nombre) {
        List<ProductoDTO> data = productoService.findByNombre(nombre);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Búsqueda exitosa", data));
    }

    @Operation(summary = "Obtener total de productos")
    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<Long>> contar() {
        Long total = productoService.contarProductos();
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Conteo total realizado", total));
    }
}