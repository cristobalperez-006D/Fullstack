package cl.duoc.pagos_service.controller;

import cl.duoc.pagos_service.dto.PagoDTO;
import cl.duoc.pagos_service.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping
    public ResponseEntity<List<PagoDTO>> obtenerTodos() {
        return ResponseEntity.ok(pagoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> obtenerPorId(@PathVariable Long id) {
        PagoDTO pago = pagoService.findById(id);
        return pago != null ? ResponseEntity.ok(pago) : ResponseEntity.notFound().build();
    }

    @PostMapping("/procesar")
    public ResponseEntity<PagoDTO> procesarPago(@RequestBody PagoDTO dto) {
        PagoDTO creado = pagoService.registrarPago(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }
}
