package cl.duoc.inventario_service.service;

import cl.duoc.inventario_service.dto.InventarioDTO;
import cl.duoc.inventario_service.model.Inventario;
import cl.duoc.inventario_service.repository.InventarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

    @Test
    void testRestarStock_Success() {
        // Given
        Long prodId = 1L;
        Inventario inv = new Inventario();
        inv.setProductoId(prodId);
        inv.setCantidad(10);

        when(inventarioRepository.findByProductoId(prodId)).thenReturn(inv);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inv);

        // When
        InventarioDTO resultado = inventarioService.restarStock(prodId, 3);

        // Then
        assertNotNull(resultado);
        assertEquals(7, resultado.getCantidad()); // 10 - 3 = 7
        verify(inventarioRepository, times(1)).save(inv);
    }

    @Test
    void testRestarStock_InsufficientStock_LanzaExcepcion() {
        // Given
        Long prodId = 1L;
        Inventario inv = new Inventario();
        inv.setProductoId(prodId);
        inv.setCantidad(2);

        when(inventarioRepository.findByProductoId(prodId)).thenReturn(inv);

        // When & Then
        assertThrows(cl.duoc.inventario_service.exception.RecursoNoEncontradoException.class, () -> {
            inventarioService.restarStock(prodId, 5); // Queremos restar 5, pero solo hay 2
        });
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }
    @Test
    void testRestarStock_ProductoNoExiste() {
        // Given
        Long prodId = 999L;
        when(inventarioRepository.findByProductoId(prodId)).thenReturn(null);

        // When & Then
        assertThrows(cl.duoc.inventario_service.exception.RecursoNoEncontradoException.class, () -> {
            inventarioService.restarStock(prodId, 1);
        });
    }

    // --- TEST 4: Buscar por ProductoId exitosamente ---
    @Test
    void testFindByProductoId_Success() {
        Long prodId = 1L;
        Inventario inv = new Inventario();
        inv.setProductoId(prodId);
        inv.setCantidad(10);
        when(inventarioRepository.findByProductoId(prodId)).thenReturn(inv);

        var resultado = inventarioService.findByProductoId(prodId);

        assertNotNull(resultado);
        assertEquals(prodId, resultado.getProductoId());
    }

    // --- TEST 5: Guardar nuevo registro ---
    @Test
    void testSave_Success() {
        InventarioDTO dto = new InventarioDTO();
        dto.setProductoId(1L);
        dto.setCantidad(10);

        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(i -> i.getArguments()[0]);

        var resultado = inventarioService.save(dto);

        assertNotNull(resultado);
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
    }

    // --- TEST 6: Sumar stock exitosamente ---
    @Test
    void testSumarStock_Success() {
        Long prodId = 1L;
        Inventario inv = new Inventario();
        inv.setProductoId(prodId);
        inv.setCantidad(5);
        when(inventarioRepository.findByProductoId(prodId)).thenReturn(inv);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inv);

        var resultado = inventarioService.sumarStock(prodId, 5);

        assertEquals(10, resultado.getCantidad());
        verify(inventarioRepository).save(inv);
    }

    // --- TEST 7: Listar todo el inventario ---
    @Test
    void testFindAll_Success() {
        when(inventarioRepository.findAll()).thenReturn(java.util.List.of(new Inventario(), new Inventario()));

        var resultado = inventarioService.findAll();

        assertEquals(2, resultado.size());
        verify(inventarioRepository, times(1)).findAll();
    }
}