package cl.duoc.envios_service.controller;
import cl.duoc.envios_service.dto.EnvioDTO;
import cl.duoc.envios_service.service.EnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @GetMapping
    public ResponseEntity<List<EnvioDTO>> obtenerTodos() {
        return ResponseEntity.ok(envioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvioDTO> obtenerPorId(@PathVariable Long id) {
        EnvioDTO envio = envioService.findById(id);
        return envio != null ? ResponseEntity.ok(envio) : ResponseEntity.notFound().build();
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<EnvioDTO> obtenerPorPedido(@PathVariable Long pedidoId) {
        EnvioDTO envio = envioService.findByPedidoId(pedidoId);
        return envio != null ? ResponseEntity.ok(envio) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<EnvioDTO> crear(@RequestBody EnvioDTO dto) {
        EnvioDTO creado = envioService.save(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    // Petición PUT para cambiar el estado rápido (ej: de PENDIENTE a DESPACHADO)
    @PutMapping("/{id}/estado")
    public ResponseEntity<EnvioDTO> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        EnvioDTO actualizado = envioService.updateEstado(id, estado);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        envioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
