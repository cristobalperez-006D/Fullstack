package cl.duoc.cliente_service.service;

import cl.duoc.cliente_service.dto.ClienteDTO;
import cl.duoc.cliente_service.model.Cliente;
import cl.duoc.cliente_service.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void testSave_Success() {
        // Given
        ClienteDTO dto = new ClienteDTO();
        dto.setNombre("Cristobal Perez");
        dto.setEmail("cris@duoc.cl");

        Cliente c = new Cliente();
        c.setId(1L);
        c.setNombre("Cristobal Perez");

        when(clienteRepository.save(any(Cliente.class))).thenReturn(c);

        // When
        ClienteDTO resultado = clienteService.save(dto);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void testFindById_Success() {
        // Given
        Long id = 1L;
        Cliente c = new Cliente();
        c.setId(id);
        c.setNombre("Cristobal Perez");

        when(clienteRepository.findById(id)).thenReturn(Optional.of(c));

        // When
        ClienteDTO resultado = clienteService.findById(id);

        // Then
        assertNotNull(resultado);
        assertEquals("Cristobal Perez", resultado.getNombre());
    }

    @Test
    void testDelete_Success() {
        // Given
        Long id = 1L;
        // ¡Aquí está la clave! Tienes que mockear que el cliente SÍ existe
        when(clienteRepository.existsById(id)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(id);

        // When
        clienteService.delete(id);

        // Then
        verify(clienteRepository, times(1)).deleteById(id);
    }
    @Test
    void testFindById_DebeLanzarExcepcion_CuandoNoExiste() {
        // Given
        Long idInexistente = 999L;
        when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(cl.duoc.cliente_service.exception.RecursoNoEncontradoException.class, () -> {
            clienteService.findById(idInexistente);
        });
    }

    @Test
    void testDelete_DebeLanzarExcepcion_CuandoNoExiste() {
        // Given
        Long idInexistente = 999L;
        when(clienteRepository.existsById(idInexistente)).thenReturn(false);

        // When & Then
        assertThrows(cl.duoc.cliente_service.exception.RecursoNoEncontradoException.class, () -> {
            clienteService.delete(idInexistente);
        });
    }

    @Test
    void testFindAll_Success() {
        // Given
        Cliente c1 = new Cliente();
        c1.setId(1L);
        c1.setNombre("Cliente Uno");

        Cliente c2 = new Cliente();
        c2.setId(2L);
        c2.setNombre("Cliente Dos");

        when(clienteRepository.findAll()).thenReturn(java.util.List.of(c1, c2));

        // When
        var resultado = clienteService.findAll();

        // Then
        assertEquals(2, resultado.size());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void testUpdate_Success() {
        // Given
        Long id = 1L;
        ClienteDTO dto = new ClienteDTO();
        dto.setNombre("Nuevo Nombre");

        Cliente cExistente = new Cliente();
        cExistente.setId(id);
        cExistente.setNombre("Nombre Viejo");

        when(clienteRepository.findById(id)).thenReturn(Optional.of(cExistente));
        // Mockeamos el guardado para que devuelva el mismo objeto que recibe
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        ClienteDTO resultado = clienteService.update(id, dto);

        // Then
        assertNotNull(resultado);
        assertEquals("Nuevo Nombre", resultado.getNombre());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }
}