package cl.duoc.pedidos_service.service;

import cl.duoc.pedidos_service.dto.PedidoDTO;
import cl.duoc.pedidos_service.dto.ClienteDTO;
import cl.duoc.pedidos_service.feign.ClienteFeignClient;
import cl.duoc.pedidos_service.model.Pedido;
import cl.duoc.pedidos_service.repository.PedidoRepository;
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
                .orElse(null);
    }

    public List<PedidoDTO> findByClienteId(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId).stream()
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
        }).orElse(null);
    }

    public void delete(Long id) {
        pedidoRepository.deleteById(id);
    }

    // Tu mapeador a mano de toda la vida
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
            System.out.println("No se pudo mapear el cliente por Feign: " + e.getMessage());
        }

        return dto;
    }
}
