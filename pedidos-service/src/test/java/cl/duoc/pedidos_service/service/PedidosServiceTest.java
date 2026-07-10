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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // Helper para los tests
    private Map<String, Object> crearMockResponse() {
        Map<String, Object> mockResp = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("id", 100L);
        data.put("nombre", "Cristobal Perez");
        mockResp.put("data", data);
        return mockResp;
    }

    @Test
    void testFindById_WithFeign() {
        Long pedidoId = 1L;
        Long clienteId = 100L;
        Pedido pedido = new Pedido();
        pedido.setId(pedidoId);
        pedido.setClienteId(clienteId);

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        // USAR EL MÉTODO RAW
        when(clienteFeignClient.obtenerClienteRaw(clienteId)).thenReturn(crearMockResponse());

        PedidoDTO resultado = pedidoService.findById(pedidoId);
        assertNotNull(resultado.getCliente());
        assertEquals("Cristobal Perez", resultado.getCliente().getNombre());
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

        // CORREGIDO: Usamos el método RAW y el helper crearMockResponse
        when(clienteFeignClient.obtenerClienteRaw(100L)).thenReturn(crearMockResponse());

        // When
        PedidoDTO resultado = pedidoService.crearPedido(dto);

        // Then
        assertNotNull(resultado);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void testFindById_DebeLanzarExcepcion_CuandoPedidoNoExiste() {
        // Given
        Long idInexistente = 999L;
        when(pedidoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(cl.duoc.pedidos_service.exception.RecursoNoEncontradoException.class, () -> {
            pedidoService.findById(idInexistente);
        });
    }

    @Test
    void testCrearPedido_DebeLanzarExcepcion_CuandoClienteNoExiste() {
        // Given
        PedidoDTO dto = new PedidoDTO();
        dto.setClienteId(999L);

        // CORREGIDO: Usamos el método RAW
        when(clienteFeignClient.obtenerClienteRaw(999L))
                .thenThrow(new RuntimeException("Cliente no existe"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            pedidoService.crearPedido(dto);
        });
    }

    @Test
    void testActualizarEstado_Success() {
        Long id = 1L;
        Pedido p = new Pedido();
        p.setId(id);
        p.setEstado("PENDIENTE");

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(p));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(p);
        // Necesario para el mapToDTO
        when(clienteFeignClient.obtenerClienteRaw(anyLong())).thenReturn(crearMockResponse());

        PedidoDTO resultado = pedidoService.actualizarEstado(id, "PAGADO");

        assertEquals("PAGADO", resultado.getEstado());
        verify(pedidoRepository).save(any(Pedido.class));
    }

    // --- TEST 6: Eliminar pedido exitosamente ---
    @Test
    void testDelete_Success() {
        Long id = 1L;
        when(pedidoRepository.existsById(id)).thenReturn(true);
        doNothing().when(pedidoRepository).deleteById(id);

        pedidoService.delete(id);

        verify(pedidoRepository, times(1)).deleteById(id);
    }

    // --- TEST 7: Calcular total gastado por cliente ---
    @Test
    void testCalcularTotalGastadoPorCliente_Success() {
        Long clienteId = 100L;
        Pedido p1 = new Pedido(); p1.setMontoTotal(new BigDecimal("1000"));
        Pedido p2 = new Pedido(); p2.setMontoTotal(new BigDecimal("2000"));

        when(pedidoRepository.findByClienteId(clienteId)).thenReturn(List.of(p1, p2));

        BigDecimal total = pedidoService.calcularTotalGastadoPorCliente(clienteId);

        assertEquals(new BigDecimal("3000"), total);
    }
}