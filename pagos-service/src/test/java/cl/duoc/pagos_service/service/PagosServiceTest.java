package cl.duoc.pagos_service.service;

import cl.duoc.pagos_service.dto.NotificacionDTO;
import cl.duoc.pagos_service.dto.PagoDTO;
import cl.duoc.pagos_service.feign.ClienteFeignClient;
import cl.duoc.pagos_service.feign.NotificacionFeignClient;
import cl.duoc.pagos_service.model.Pago;
import cl.duoc.pagos_service.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagosServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private NotificacionFeignClient notificacionFeignClient;

    @Mock
    private ClienteFeignClient clienteFeignClient; // <--- ¡Este faltaba en tu clase de test!

    @InjectMocks
    private PagoService pagoService;

    @Test
    void testRegistrarPago_Success() {
        // Given
        PagoDTO dto = new PagoDTO();
        dto.setPedidoId(500L);
        dto.setClienteId(10L);
        dto.setMonto(new BigDecimal("10000"));
        dto.setMetodoPago("WEBPAY");

        Pago guardado = new Pago();
        guardado.setId(1L);
        guardado.setClienteId(10L);
        guardado.setMonto(new BigDecimal("10000"));
        guardado.setPedidoId(500L);
        guardado.setEstado("APROBADO");

        when(pagoRepository.save(any(Pago.class))).thenReturn(guardado);
        // Mockeamos el cliente para que el mapToDTO no sea null
        when(clienteFeignClient.obtenerClienteRaw(anyLong())).thenReturn(new HashMap<>());

        // When
        PagoDTO resultado = pagoService.registrarPago(dto);

        // Then
        assertNotNull(resultado);
        assertEquals("APROBADO", resultado.getEstado());

        verify(notificacionFeignClient, times(1)).crear(any(NotificacionDTO.class));
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    void testRegistrarPago_DebeLanzarExcepcion_CuandoDatosSonInvalidos() {
        PagoDTO dto = new PagoDTO();
        dto.setMonto(new BigDecimal("-100"));

        assertThrows(RuntimeException.class, () -> {
            pagoService.registrarPago(dto);
        });

        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    void testRegistrarPago_DebeContinuar_AunqueNotificacionFalle() {
        // Given
        PagoDTO dto = new PagoDTO();
        dto.setMonto(new BigDecimal("1000"));
        dto.setClienteId(1L);

        when(pagoRepository.save(any(Pago.class))).thenReturn(new Pago());
        when(clienteFeignClient.obtenerClienteRaw(anyLong())).thenReturn(new HashMap<>());

        // Simulamos que el Feign de notificaciones falla
        when(notificacionFeignClient.crear(any(NotificacionDTO.class)))
                .thenThrow(new RuntimeException("Servicio de notificaciones caído"));

        // When: Llamamos al servicio
        PagoDTO resultado = pagoService.registrarPago(dto);

        // Then: Verificamos que el pago NO falló y el Service no lanzó excepción
        assertNotNull(resultado);
    }


    @Test
    void testFindById_Success() {
        Long id = 1L;
        Pago p = new Pago();
        p.setId(id);
        when(pagoRepository.findById(id)).thenReturn(Optional.of(p));
        // Mock del cliente para el mapeo a DTO
        when(clienteFeignClient.obtenerClienteRaw(anyLong())).thenReturn(new HashMap<>());

        PagoDTO resultado = pagoService.findById(id);
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
    }

    @Test
    void testFindByPedidoId_Success() {
        Long pedidoId = 500L;
        Pago p = new Pago();
        p.setPedidoId(pedidoId);
        when(pagoRepository.findByPedidoId(pedidoId)).thenReturn(List.of(p));
        when(clienteFeignClient.obtenerClienteRaw(anyLong())).thenReturn(new HashMap<>());

        List<PagoDTO> resultado = pagoService.findByPedidoId(pedidoId);
        assertFalse(resultado.isEmpty());
        assertEquals(pedidoId, resultado.get(0).getPedidoId());
    }

    @Test
    void testCalcularTotalPagadoPorCliente_Success() {
        Long clienteId = 1L;
        Pago p1 = new Pago(); p1.setMonto(new BigDecimal("1000"));
        Pago p2 = new Pago(); p2.setMonto(new BigDecimal("2000"));
        when(pagoRepository.findByClienteId(clienteId)).thenReturn(List.of(p1, p2));

        BigDecimal total = pagoService.calcularTotalPagadoPorCliente(clienteId);
        assertEquals(new BigDecimal("3000"), total);
    }

    @Test
    void testDelete_Success() {
        Long id = 1L;
        when(pagoRepository.existsById(id)).thenReturn(true);
        doNothing().when(pagoRepository).deleteById(id);

        pagoService.delete(id);
        verify(pagoRepository, times(1)).deleteById(id);
    }
}