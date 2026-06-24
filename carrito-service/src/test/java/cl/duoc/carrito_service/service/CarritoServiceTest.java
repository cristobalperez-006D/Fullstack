package cl.duoc.carrito_service.service;

import cl.duoc.carrito_service.dto.CarritoDTO;
import cl.duoc.carrito_service.dto.ClienteDTO;
import cl.duoc.carrito_service.dto.ProductoDTO;
import cl.duoc.carrito_service.feign.ClienteFeignClient;
import cl.duoc.carrito_service.feign.ProductoFeignClient;
import cl.duoc.carrito_service.model.Carrito;
import cl.duoc.carrito_service.repository.CarritoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ClienteFeignClient clienteFeignClient;

    @Mock
    private ProductoFeignClient productoFeignClient;

    @InjectMocks
    private CarritoService carritoService;

    @Test
    void testAgregarItem_SumaCantidad_NuevoItem() {
        // Given
        CarritoDTO dto = new CarritoDTO();
        dto.setClienteId(1L); // <--- Asegúrate que esto no sea null
        dto.setProductoId(10L); // <--- Asegúrate que esto no sea null
        dto.setCantidad(2);

        Carrito itemExistente = new Carrito();
        itemExistente.setClienteId(1L); // IMPORTANTE: setea estos campos
        itemExistente.setProductoId(10L);
        itemExistente.setCantidad(1);

        when(carritoRepository.findByClienteIdAndProductoId(1L, 10L)).thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(i -> i.getArguments()[0]);
        // Mockeamos los Feign para que el mapToDTO no tire error
        when(clienteFeignClient.obtenerClientePorId(1L)).thenReturn(new ClienteDTO());
        when(productoFeignClient.obtenerProductoPorId(10L)).thenReturn(new ProductoDTO());

        // When
        CarritoDTO resultado = carritoService.agregarItem(dto);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.getCantidad());
        verify(carritoRepository, times(1)).save(any(Carrito.class));
    }

    @Test
    void testAgregarItem_SumaCantidad() {
        // Given
        CarritoDTO dto = new CarritoDTO();
        dto.setClienteId(1L);
        dto.setProductoId(10L);
        dto.setCantidad(2);

        Carrito itemExistente = new Carrito();
        itemExistente.setClienteId(1L);   // <--- ¡Esto faltaba!
        itemExistente.setProductoId(10L); // <--- ¡Esto faltaba!
        itemExistente.setCantidad(1);

        when(carritoRepository.findByClienteIdAndProductoId(1L, 10L)).thenReturn(Optional.of(itemExistente));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(i -> i.getArguments()[0]);

        // Mockeos lenientes para evitar el error de "argument mismatch"
        lenient().when(clienteFeignClient.obtenerClientePorId(anyLong())).thenReturn(new ClienteDTO());
        lenient().when(productoFeignClient.obtenerProductoPorId(anyLong())).thenReturn(new ProductoDTO());

        // When
        CarritoDTO resultado = carritoService.agregarItem(dto);

        // Then
        assertEquals(3, resultado.getCantidad());
    }
}