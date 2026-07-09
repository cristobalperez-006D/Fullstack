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
}