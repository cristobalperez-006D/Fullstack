package cl.duoc.producto_service.service;

import cl.duoc.producto_service.dto.ProductoDTO;
import cl.duoc.producto_service.model.Producto;
import cl.duoc.producto_service.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void testFindAll_Success() {
        // Given
        Producto p1 = new Producto(); p1.setNombre("Monitor");
        Producto p2 = new Producto(); p2.setNombre("Teclado");
        when(productoRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        // When
        List<ProductoDTO> resultado = productoService.findAll();

        // Then
        assertEquals(2, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testSave_Success() {
        // Given
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Mouse");
        dto.setPrecio(new BigDecimal("15000"));

        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Mouse");

        when(productoRepository.save(any(Producto.class))).thenReturn(p);

        // When
        ProductoDTO resultado = productoService.save(dto);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void testFindById_Success() {
        // Given
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Webcam");
        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));

        // When
        ProductoDTO resultado = productoService.findById(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("Webcam", resultado.getNombre());
    }
}