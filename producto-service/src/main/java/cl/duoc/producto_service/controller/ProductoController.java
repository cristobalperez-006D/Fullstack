package cl.duoc.producto_service.controller;

import cl.duoc.producto_service.dto.ProductoDTO;
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
@RequestMapping("/api/productos")
@Tag(name = "Catálogo de Productos", description = "API dedicada a la gestión integral del inventario de productos. Permite realizar operaciones CRUD con alta disponibilidad y coherencia de datos para el ecosistema microservicios.")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(
            summary = "Listado maestro de productos",
            description = "Extrae la colección completa de productos activos disponibles en el catálogo central. Implementado para alimentar interfaces de usuario y sistemas de búsqueda."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catálogo recuperado exitosamente.")
    })
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerTodos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @Operation(
            summary = "Recuperar detalles técnicos del producto",
            description = "Ejecuta un lookup de alta velocidad para obtener las especificaciones y metadata de un producto por su ID único. Endpoint fundamental para la comunicación entre servicios vía Feign."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto localizado correctamente."),
            @ApiResponse(responseCode = "404", description = "El producto no existe en el catálogo.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerProductoPorId(
            @Parameter(description = "ID del producto consultado", example = "101")
            @PathVariable Long id
    ) {
        ProductoDTO producto = productoService.findById(id);
        return producto != null ? ResponseEntity.ok(producto) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Aprovisionar nuevo producto",
            description = "Registra una nueva entidad de producto en el ecosistema, validando su estructura de datos y persistiendo el nuevo SKU en la base de datos central."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto registrado e integrado al inventario."),
            @ApiResponse(responseCode = "400", description = "Error de validación en el payload del producto.")
    })
    @PostMapping
    public ResponseEntity<ProductoDTO> crear(
            @Parameter(description = "DTO con los datos del nuevo producto")
            @RequestBody ProductoDTO dto
    ) {
        ProductoDTO creado = productoService.save(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Actualizar atributos del producto",
            description = "Modifica la información existente de un producto mediante un update transaccional. Asegura la coherencia de datos entre el inventario y el catálogo visual."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado satisfactoriamente."),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado para la actualización.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(
            @Parameter(description = "ID del producto a actualizar", example = "101")
            @PathVariable Long id,
            @RequestBody ProductoDTO dto
    ) {
        ProductoDTO actualizado = productoService.update(id, dto);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Descontinuar/Eliminar producto",
            description = "Ejecuta el retiro definitivo de un producto del ecosistema, aplicando las reglas de negocio correspondientes para evitar inconsistencias en el carrito de compras."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente del catálogo.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del producto a eliminar", example = "101")
            @PathVariable Long id
    ) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}