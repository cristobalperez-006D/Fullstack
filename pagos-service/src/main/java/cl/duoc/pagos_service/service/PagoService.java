package cl.duoc.pagos_service.service;

import cl.duoc.pagos_service.dto.PagoDTO;
import cl.duoc.pagos_service.dto.NotificacionDTO;
import cl.duoc.pagos_service.feign.NotificacionFeignClient;
import cl.duoc.pagos_service.model.Pago;
import cl.duoc.pagos_service.repository.PagoRepository;
import cl.duoc.pagos_service.exception.RecursoNoEncontradoException; // ¡Importa tu excepción!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private NotificacionFeignClient notificacionFeignClient;

    public List<PagoDTO> findAll() {
        return pagoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PagoDTO findById(Long id) {
        return pagoRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró registro del pago con ID: " + id));
    }

    public PagoDTO registrarPago(PagoDTO dto) {
        Pago pago = new Pago();
        pago.setPedidoId(dto.getPedidoId());
        pago.setClienteId(dto.getClienteId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstado(dto.getEstado() != null ? dto.getEstado() : "APROBADO");
        pago.setFechaPago(LocalDateTime.now());

        Pago guardado = pagoRepository.save(pago);

        // Intentamos notificar, pero si falla Feign, lanzamos error para que el usuario sepa que algo pasó
        try {
            NotificacionDTO notiDto = new NotificacionDTO();
            notiDto.setClienteId(guardado.getClienteId());
            notiDto.setTipo("EMAIL");
            notiDto.setMensaje("¡Hola! Tu pago de $" + guardado.getMonto() + " para el pedido #" + guardado.getPedidoId() + " ha sido " + guardado.getEstado() + " exitosamente.");

            notificacionFeignClient.crear(notiDto);
        } catch (Exception e) {
            // Aquí lanzamos una excepción técnica si el sistema de notificaciones no responde
            throw new RuntimeException("Pago registrado, pero falló el envío de la notificación: " + e.getMessage());
        }

        return mapToDTO(guardado);
    }

    private PagoDTO mapToDTO(Pago pago) {
        PagoDTO dto = new PagoDTO();
        dto.setId(pago.getId());
        dto.setPedidoId(pago.getPedidoId());
        dto.setClienteId(pago.getClienteId());
        dto.setMonto(pago.getMonto());
        dto.setMetodoPago(pago.getMetodoPago());
        dto.setEstado(pago.getEstado());
        dto.setFechaPago(pago.getFechaPago());
        return dto;
    }
}