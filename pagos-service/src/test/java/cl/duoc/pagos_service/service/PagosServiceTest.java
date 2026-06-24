package cl.duoc.pagos_service.service;

import cl.duoc.pagos_service.dto.NotificacionDTO;
import cl.duoc.pagos_service.dto.PagoDTO;
import cl.duoc.pagos_service.feign.NotificacionFeignClient;
import cl.duoc.pagos_service.model.Pago;
import cl.duoc.pagos_service.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagosServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private NotificacionFeignClient notificacionFeignClient;

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

        // When
        PagoDTO resultado = pagoService.registrarPago(dto);

        // Then
        assertNotNull(resultado);
        assertEquals("APROBADO", resultado.getEstado());

        // Verificamos que el Feign fue llamado al menos una vez para avisar
        verify(notificacionFeignClient, times(1)).crear(any(NotificacionDTO.class));
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }
}