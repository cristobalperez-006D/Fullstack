package cl.duoc.pagos_service.service;

import cl.duoc.pagos_service.dto.*;
import cl.duoc.pagos_service.feign.ClienteFeignClient;
import cl.duoc.pagos_service.feign.NotificacionFeignClient;
import cl.duoc.pagos_service.model.Pago;
import cl.duoc.pagos_service.repository.PagoRepository;
import cl.duoc.pagos_service.exception.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private NotificacionFeignClient notificacionFeignClient;

    @Autowired
    private ClienteFeignClient clienteFeignClient;

    public List<PagoDTO> findAll() {
        return pagoRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public PagoDTO findById(Long id) {
        return pagoRepository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró registro del pago con ID: " + id));
    }

    public PagoDTO registrarPago(PagoDTO dto) {
        if (dto.getMonto() == null || dto.getMonto().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto debe ser mayor a cero.");
        }
        Pago pago = new Pago();
        pago.setPedidoId(dto.getPedidoId());
        pago.setClienteId(dto.getClienteId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstado(dto.getEstado() != null ? dto.getEstado() : "APROBADO");
        pago.setFechaPago(LocalDateTime.now());

        Pago guardado = pagoRepository.save(pago);

        // Envío de notificación (ahora usa el cliente Raw)
        try {
            NotificacionDTO notiDto = new NotificacionDTO();
            notiDto.setClienteId(guardado.getClienteId());
            notiDto.setTipo("EMAIL");
            notiDto.setMensaje("¡Hola! Tu pago de $" + guardado.getMonto() + " para el pedido #" + guardado.getPedidoId() + " ha sido " + guardado.getEstado() + " exitosamente.");
            notificacionFeignClient.crear(notiDto);
        } catch (Exception e) {
            System.err.println("Pago registrado, pero la notificación falló: " + e.getMessage());
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

        // DESEMPAQUETADO DEL CLIENTE (Misma lógica del Carrito)
        try {
            Map<String, Object> resp = clienteFeignClient.obtenerClienteRaw(pago.getClienteId());
            if (resp != null && resp.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) resp.get("data");
                ClienteDTO c = new ClienteDTO();
                c.setId(Long.valueOf(data.get("id").toString()));
                c.setNombre((String) data.get("nombre"));
                c.setEmail((String) data.get("email"));
                dto.setCliente(c);
            }
        } catch (Exception e) {
            dto.setCliente(null);
        }

        return dto;
    }

    public List<PagoDTO> findByPedidoId(Long pedidoId) {
        return pagoRepository.findByPedidoId(pedidoId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<PagoDTO> findByClienteId(Long clienteId) {
        return pagoRepository.findByClienteId(clienteId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public java.math.BigDecimal calcularTotalPagadoPorCliente(Long clienteId) {
        return pagoRepository.findByClienteId(clienteId).stream()
                .map(Pago::getMonto).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public void delete(Long id) {
        if (!pagoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No se puede eliminar: Pago ID " + id + " no encontrado.");
        }
        pagoRepository.deleteById(id);
    }
}