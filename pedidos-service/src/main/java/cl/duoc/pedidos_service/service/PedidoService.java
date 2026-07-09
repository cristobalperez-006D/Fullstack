package cl.duoc.pedidos_service.service;

import cl.duoc.pedidos_service.dto.PedidoDTO;
import cl.duoc.pedidos_service.dto.ClienteDTO;
import cl.duoc.pedidos_service.feign.ClienteFeignClient;
import cl.duoc.pedidos_service.model.Pedido;
import cl.duoc.pedidos_service.repository.PedidoRepository;
import cl.duoc.pedidos_service.exception.RecursoNoEncontradoException; // ¡Importa tu excepción!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteFeignClient clienteFeignClient;

    public List<PedidoDTO> findAll() {
        return pedidoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PedidoDTO findById(Long id) {
        return pedidoRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el pedido con ID: " + id));
    }

    public List<PedidoDTO> findByClienteId(Long clienteId) {
        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        if (pedidos.isEmpty()) {
            throw new RecursoNoEncontradoException("No existen pedidos registrados para el cliente ID: " + clienteId);
        }
        return pedidos.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PedidoDTO crearPedido(PedidoDTO dto) {
        // Validar existencia vía Feign RAW
        try {
            Map<String, Object> resp = clienteFeignClient.obtenerClienteRaw(dto.getClienteId());
            if (resp == null || !resp.containsKey("data")) {
                throw new RecursoNoEncontradoException("Cliente no existe.");
            }
        } catch (Exception e) {
            throw new RecursoNoEncontradoException("Error al validar cliente: " + e.getMessage());
        }

        Pedido pedido = new Pedido();
        pedido.setClienteId(dto.getClienteId());
        pedido.setMontoTotal(dto.getMontoTotal());
        pedido.setEstado(dto.getEstado() != null ? dto.getEstado() : "PENDIENTE");
        pedido.setFechaPedido(LocalDateTime.now());
        return mapToDTO(pedidoRepository.save(pedido));
    }

    public PedidoDTO actualizarEstado(Long id, String nuevoEstado) {
        return pedidoRepository.findById(id).map(p -> {
            p.setEstado(nuevoEstado);
            Pedido actualizado = pedidoRepository.save(p);
            return mapToDTO(actualizado);
        }).orElseThrow(() -> new RecursoNoEncontradoException("Imposible actualizar: El pedido ID " + id + " no existe."));
    }

    public void delete(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No se puede eliminar: Pedido ID " + id + " no encontrado.");
        }
        pedidoRepository.deleteById(id);
    }

    private PedidoDTO mapToDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setClienteId(pedido.getClienteId());
        dto.setMontoTotal(pedido.getMontoTotal());
        dto.setEstado(pedido.getEstado());
        dto.setFechaPedido(pedido.getFechaPedido());

        // Jalamos la info del cliente vía Feign
        try {
            Map<String, Object> resp = clienteFeignClient.obtenerClienteRaw(pedido.getClienteId());
            if (resp != null && resp.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) resp.get("data");
                ClienteDTO c = new ClienteDTO();
                c.setId(Long.valueOf(data.get("id").toString()));
                c.setNombre((String) data.get("nombre"));
                c.setEmail((String) data.get("email"));
                dto.setCliente(c);
            }
        } catch (Exception e) { dto.setCliente(null); }
        return dto;
    }
    public java.math.BigDecimal calcularTotalGastadoPorCliente(Long clienteId) {
        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        if (pedidos.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay pedidos para calcular el gasto del cliente: " + clienteId);
        }
        return pedidos.stream()
                .map(Pedido::getMontoTotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}