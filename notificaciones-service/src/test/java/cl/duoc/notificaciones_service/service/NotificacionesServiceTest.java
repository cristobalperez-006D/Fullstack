package cl.duoc.notificaciones_service.service;

import cl.duoc.notificaciones_service.dto.NotificacionDTO;
import cl.duoc.notificaciones_service.model.Notificacion;
import cl.duoc.notificaciones_service.repository.NotificacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificacionesServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    @Test
    void testSave_Success() {
        // Given
        NotificacionDTO dto = new NotificacionDTO();
        dto.setClienteId(1L);
        dto.setMensaje("Hola tobal, tu pedido viene en camino");
        dto.setTipo("EMAIL");

        Notificacion guardada = new Notificacion();
        guardada.setId(10L);
        guardada.setClienteId(1L);
        guardada.setFechaEnvio(LocalDateTime.now());

        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(guardada);

        // When
        NotificacionDTO resultado = notificacionService.save(dto);

        // Then
        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    @Test
    void testFindByClienteId() {
        // Given
        Notificacion n1 = new Notificacion();
        n1.setClienteId(1L);
        when(notificacionRepository.findByClienteId(1L)).thenReturn(Arrays.asList(n1));

        // When
        List<NotificacionDTO> resultado = notificacionService.findByClienteId(1L);

        // Then
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getClienteId());
    }
}