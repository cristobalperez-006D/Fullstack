package cl.duoc.envios_service.service;

import cl.duoc.envios_service.dto.EnvioDTO;
import cl.duoc.envios_service.model.Envio;
import cl.duoc.envios_service.repository.EnvioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnviosServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @InjectMocks
    private EnvioService envioService;

    @Test
    void testSave_Success() {
        // Given
        EnvioDTO dto = new EnvioDTO();
        dto.setPedidoId(99L);
        dto.setDireccionDestino("Av. Vicuña Mackenna 123");
        dto.setCodigoSeguimiento("TRK-001");

        Envio guardado = new Envio();
        guardado.setId(1L);
        guardado.setEstado("PENDIENTE");

        when(envioRepository.save(any(Envio.class))).thenReturn(guardado);

        // When
        EnvioDTO resultado = envioService.save(dto);

        // Then
        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
        verify(envioRepository, times(1)).save(any(Envio.class));
    }

    @Test
    void testUpdateEstado_Success() {
        // Given
        Long envioId = 1L;
        Envio e = new Envio();
        e.setId(envioId);
        e.setEstado("PENDIENTE");

        when(envioRepository.findById(envioId)).thenReturn(Optional.of(e));
        when(envioRepository.save(any(Envio.class))).thenReturn(e);

        // When
        EnvioDTO resultado = envioService.updateEstado(envioId, "EN_CAMINO");

        // Then
        assertNotNull(resultado);
        verify(envioRepository, times(1)).save(e);
    }
    @Test
    void testFindById_DebeLanzarExcepcion_CuandoNoExiste() {
        // Given
        Long idInexistente = 999L;
        when(envioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(cl.duoc.envios_service.exception.RecursoNoEncontradoException.class, () -> {
            envioService.findById(idInexistente);
        });
    }

    @Test
    void testUpdateEstado_DebeLanzarExcepcion_CuandoNoExiste() {
        // Given
        Long idInexistente = 999L;
        when(envioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(cl.duoc.envios_service.exception.RecursoNoEncontradoException.class, () -> {
            envioService.updateEstado(idInexistente, "ENTREGADO");
        });
    }
    @Test
    void testFindById_Success() {
        Long id = 1L;
        Envio e = new Envio();
        e.setId(id);
        e.setEstado("PENDIENTE");

        when(envioRepository.findById(id)).thenReturn(Optional.of(e));

        EnvioDTO resultado = envioService.findById(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
    }

    @Test
    void testDelete_Success() {
        Long id = 1L;
        when(envioRepository.existsById(id)).thenReturn(true);
        doNothing().when(envioRepository).deleteById(id);

        envioService.delete(id);

        verify(envioRepository, times(1)).deleteById(id);
    }

    @Test
    void testFindAll_Success() {
        Envio e1 = new Envio();
        Envio e2 = new Envio();

        when(envioRepository.findAll()).thenReturn(List.of(e1, e2));

        List<EnvioDTO> resultado = envioService.findAll();

        assertEquals(2, resultado.size());
        verify(envioRepository, times(1)).findAll();
    }
}
