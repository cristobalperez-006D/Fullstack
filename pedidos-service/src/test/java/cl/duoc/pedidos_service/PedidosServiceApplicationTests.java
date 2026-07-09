package cl.duoc.pedidos_service.service;

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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidosServiceApplicationTests {

	@Mock
	private PedidoRepository pedidoRepository;

	@Mock
	private ClienteFeignClient clienteFeignClient;

	@InjectMocks
	private PedidoService pedidoService;

	// Helper para el formato que espera el Service ahora
	private Map<String, Object> crearMockResponse() {
		Map<String, Object> mockResp = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		data.put("id", 100L);
		data.put("nombre", "Cristobal Perez");
		data.put("email", "cris@duoc.cl");
		mockResp.put("data", data);
		return mockResp;
	}

	@Test
	void testFindById_WithFeign() {
		// Given
		Long pedidoId = 1L;
		Long clienteId = 100L;

		Pedido pedido = new Pedido();
		pedido.setId(pedidoId);
		pedido.setClienteId(clienteId);

		when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

		// CORREGIDO: Usamos el método RAW
		when(clienteFeignClient.obtenerClienteRaw(clienteId)).thenReturn(crearMockResponse());

		// When
		PedidoDTO resultado = pedidoService.findById(pedidoId);

		// Then
		assertNotNull(resultado);
		assertNotNull(resultado.getCliente());
		assertEquals("Cristobal Perez", resultado.getCliente().getNombre());
		verify(clienteFeignClient, times(1)).obtenerClienteRaw(clienteId);
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

		// CORREGIDO: Usamos el método RAW y el helper
		when(clienteFeignClient.obtenerClienteRaw(100L)).thenReturn(crearMockResponse());

		// When
		PedidoDTO resultado = pedidoService.crearPedido(dto);

		// Then
		assertNotNull(resultado);
		verify(pedidoRepository, times(1)).save(any(Pedido.class));
	}
}