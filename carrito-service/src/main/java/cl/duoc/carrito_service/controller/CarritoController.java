package cl.duoc.carrito_service.controller;

import cl.duoc.carrito_service.dto.CarritoDTO;
import cl.duoc.carrito_service.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @PostMapping("/agregar")
    public ResponseEntity<CarritoDTO> agregarItem(@RequestBody CarritoDTO carritoDTO) {
        CarritoDTO response = carritoService.agregarItem(carritoDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CarritoDTO>> obtenerCarrito(@PathVariable Long clienteId) {
        return ResponseEntity.ok(carritoService.obtenerCarritoPorCliente(clienteId));
    }

    @DeleteMapping("/eliminar/{itemId}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long itemId) {
        carritoService.eliminarItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/vaciar/{clienteId}")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable Long clienteId) {
        carritoService.vaciarCarrito(clienteId);
        return ResponseEntity.noContent().build();
    }
}