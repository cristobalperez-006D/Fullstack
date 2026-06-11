package cl.duoc.inventario_service.controller;
import cl.duoc.inventario_service.dto.InventarioDTO;
import cl.duoc.inventario_service.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<InventarioDTO>> obtenerTodos() {
        return ResponseEntity.ok(inventarioService.findAll());
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<InventarioDTO> obtenerPorProducto(@PathVariable Long productoId) {
        InventarioDTO inv = inventarioService.findByProductoId(productoId);
        return inv != null ? ResponseEntity.ok(inv) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<InventarioDTO> crear(@RequestBody InventarioDTO dto) {
        InventarioDTO creado = inventarioService.save(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    // Un PUT exclusivo pa' cuando se concrete una venta
    @PutMapping("/restar/{productoId}")
    public ResponseEntity<InventarioDTO> restarStock(@PathVariable Long productoId, @RequestParam Integer cantidad) {
        InventarioDTO actualizado = inventarioService.restarStock(productoId, cantidad);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.badRequest().build(); // Tiramos un 400 si intentan sacar más de lo que hay
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}