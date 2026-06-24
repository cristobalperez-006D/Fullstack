package cl.duoc.pedidos_service.service;

import cl.duoc.pedidos_service.dto.ClienteDTO;
import cl.duoc.pedidos_service.dto.PedidoDTO;
import cl.duoc.pedidos_service.feign.ClienteFeignClient;
import cl.duoc.pedidos_service.model.Pedido;
import cl.duoc.pedidos_service.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidosServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteFeignClient clienteFeignClient;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void testFindById_WithFeign() {
        // Given: Preparamos un pedido y un cliente mockeado
        Long pedidoId = 1L;
        Long clienteId = 100L;

        Pedido pedido = new Pedido();
        pedido.setId(pedidoId);
        pedido.setClienteId(clienteId);

        ClienteDTO mockCliente = new ClienteDTO();
        mockCliente.setId(clienteId);
        mockCliente.setNombre("Cristobal Perez");

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        // Aquí le decimos a Mockito: "Cuando llamen al Feign, devuelve el mockCliente"
        when(clienteFeignClient.obtenerClientePorId(clienteId)).thenReturn(mockCliente);

        // When
        PedidoDTO resultado = pedidoService.findById(pedidoId);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getCliente());
        assertEquals("Cristobal Perez", resultado.getCliente().getNombre());
        verify(clienteFeignClient, times(1)).obtenerClientePorId(clienteId);
    }

    @Test
    void testCrearPedido() {
        // Given
        PedidoDTO dto = new PedidoDTO();
        dto.setClienteId(100L);
        dto.setMontoTotal(new BigDecimal("50000"));

        Pedido pedidoGuardado = new Pedido();
        pedidoGuardado.setId(1L);
        pedidoGuardado.setClienteId(100L);

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);
        // También mockeamos el cliente para que el mapToDTO no tire error
        when(clienteFeignClient.obtenerClientePorId(100L)).thenReturn(new ClienteDTO());

        // When
        PedidoDTO resultado = pedidoService.crearPedido(dto);

        // Then
        assertNotNull(resultado);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }
}