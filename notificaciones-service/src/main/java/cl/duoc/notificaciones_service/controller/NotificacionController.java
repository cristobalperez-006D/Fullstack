package cl.duoc.notificaciones_service.controller;

import cl.duoc.notificaciones_service.dto.NotificacionDTO;
import cl.duoc.notificaciones_service.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> obtenerTodas() {
        return ResponseEntity.ok(notificacionService.findAll());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<NotificacionDTO>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<NotificacionDTO> notificaciones = notificacionService.findByClienteId(clienteId);
        return ResponseEntity.ok(notificaciones);
    }

    @PostMapping
    public ResponseEntity<NotificacionDTO> crear(@RequestBody NotificacionDTO dto) {
        NotificacionDTO creada = notificacionService.save(dto);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
