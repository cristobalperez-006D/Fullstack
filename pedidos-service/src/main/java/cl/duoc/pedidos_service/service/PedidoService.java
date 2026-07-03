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
        Pedido pedido = new Pedido();
        pedido.setClienteId(dto.getClienteId());
        pedido.setMontoTotal(dto.getMontoTotal());
        pedido.setEstado(dto.getEstado() != null ? dto.getEstado() : "PENDIENTE");
        pedido.setFechaPedido(LocalDateTime.now());

        Pedido guardado = pedidoRepository.save(pedido);
        return mapToDTO(guardado);
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
            ClienteDTO cliente = clienteFeignClient.obtenerClientePorId(pedido.getClienteId());
            dto.setCliente(cliente);
        } catch (Exception e) {
            // Lanzamos la excepción para que el GlobalExceptionHandler la capture y devuelva el JSON pro
            throw new RecursoNoEncontradoException("No se pudo vincular el cliente ID [" + pedido.getClienteId() + "] al pedido.");
        }

        return dto;
    }
}