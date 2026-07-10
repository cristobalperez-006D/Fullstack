package cl.duoc.notificaciones_service.service;

import cl.duoc.notificaciones_service.dto.NotificacionDTO;
import cl.duoc.notificaciones_service.exception.RecursoNoEncontradoException;
import cl.duoc.notificaciones_service.model.Notificacion;
import cl.duoc.notificaciones_service.repository.NotificacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
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
        NotificacionDTO dto = new NotificacionDTO();
        dto.setClienteId(1L);
        dto.setMensaje("Test");
        dto.setTipo("EMAIL");

        Notificacion guardada = new Notificacion();
        guardada.setId(10L);
        guardada.setClienteId(1L);

        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(guardada);

        NotificacionDTO resultado = notificacionService.save(dto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
    }

    @Test
    void testFindByClienteId_Success() {
        Notificacion n1 = new Notificacion();
        n1.setClienteId(1L);
        when(notificacionRepository.findByClienteId(1L)).thenReturn(List.of(n1));

        List<NotificacionDTO> resultado = notificacionService.findByClienteId(1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void testFindByClienteId_LanzaExcepcion_CuandoNoHay() {
        when(notificacionRepository.findByClienteId(1L)).thenReturn(Collections.emptyList());
        assertThrows(RecursoNoEncontradoException.class, () -> notificacionService.findByClienteId(1L));
    }

    @Test
    void testDelete_Success() {
        Long id = 10L;
        when(notificacionRepository.existsById(id)).thenReturn(true);
        doNothing().when(notificacionRepository).deleteById(id);

        notificacionService.delete(id);
        verify(notificacionRepository, times(1)).deleteById(id);
    }

    @Test
    void testDelete_LanzaExcepcion_CuandoNoExiste() {
        when(notificacionRepository.existsById(999L)).thenReturn(false);
        assertThrows(RecursoNoEncontradoException.class, () -> notificacionService.delete(999L));
    }

    @Test
    void testContarPorCliente() {
        when(notificacionRepository.countByClienteId(1L)).thenReturn(5L);
        Long cantidad = notificacionService.contarPorCliente(1L);
        assertEquals(5L, cantidad);
    }

    @Test
    void testLimpiarNotificacionesAntiguas() {
        Notificacion n = new Notificacion();
        n.setFechaEnvio(LocalDateTime.now().minusDays(40)); // Más de 30 días
        when(notificacionRepository.findByClienteId(1L)).thenReturn(List.of(n));

        notificacionService.limpiarNotificacionesAntiguas(1L);
        verify(notificacionRepository, times(1)).deleteAll(anyList());
    }
}
