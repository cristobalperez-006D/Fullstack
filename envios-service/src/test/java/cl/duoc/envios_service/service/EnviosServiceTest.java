package cl.duoc.envios_service.service;

import cl.duoc.envios_service.dto.EnvioDTO;
import cl.duoc.envios_service.model.Envio;
import cl.duoc.envios_service.repository.EnvioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}