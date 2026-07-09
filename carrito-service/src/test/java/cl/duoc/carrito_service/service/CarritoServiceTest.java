package cl.duoc.carrito_service.service;

import cl.duoc.carrito_service.dto.CarritoDTO;
import cl.duoc.carrito_service.model.Carrito;
import cl.duoc.carrito_service.repository.CarritoRepository;
import cl.duoc.carrito_service.feign.ClienteFeignClient;
import cl.duoc.carrito_service.feign.ProductoFeignClient;
import cl.duoc.carrito_service.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarritoServiceTest {

    @Mock private CarritoRepository carritoRepository;
    @Mock private ClienteFeignClient clienteFeignClient;
    @Mock private ProductoFeignClient productoFeignClient;

    @InjectMocks
    private CarritoService carritoService;

    // Helper para crear el mapa de respuesta que espera el Service
    private Map<String, Object> crearMockResponse() {
        Map<String, Object> mockResp = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1L);
        data.put("nombre", "Test");
        data.put("email", "test@test.cl");
        data.put("precio", 1000);
        mockResp.put("data", data);
        return mockResp;
    }

    @Test
    void testAgregarItem_SumaCantidad_NuevoItem() {
        CarritoDTO dto = new CarritoDTO();
        dto.setClienteId(1L);
        dto.setProductoId(10L);
        dto.setCantidad(2);
        dto.setPrecioUnitario(new BigDecimal("1500"));

        when(carritoRepository.findByClienteIdAndProductoId(1L, 10L)).thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(i -> i.getArguments()[0]);

        // Usamos los métodos RAW
        when(clienteFeignClient.obtenerClienteRaw(1L)).thenReturn(crearMockResponse());
        when(productoFeignClient.obtenerProductoRaw(10L)).thenReturn(crearMockResponse());

        CarritoDTO resultado = carritoService.agregarItem(dto);
        assertNotNull(resultado);
        assertEquals(2, resultado.getCantidad());
    }

    @Test
    void testAgregarItem_SumaCantidad() {
        CarritoDTO dto = new CarritoDTO();
        dto.setClienteId(1L);
        dto.setProductoId(10L);
        dto.setCantidad(2);

        Carrito itemExistente = new Carrito();
        itemExistente.setClienteId(1L);
        itemExistente.setProductoId(10L);
        itemExistente.setCantidad(1);

        when(carritoRepository.findByClienteIdAndProductoId(1L, 10L)).thenReturn(Optional.of(itemExistente));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(i -> i.getArguments()[0]);

        // Mockeos corregidos para la nueva lógica RAW
        lenient().when(clienteFeignClient.obtenerClienteRaw(anyLong())).thenReturn(crearMockResponse());
        lenient().when(productoFeignClient.obtenerProductoRaw(anyLong())).thenReturn(crearMockResponse());

        CarritoDTO resultado = carritoService.agregarItem(dto);
        assertEquals(3, resultado.getCantidad());
    }

    @Test
    void testEliminarItem_DebeLanzarExcepcion_CuandoNoExiste() {
        Long idInexistente = 999L;
        when(carritoRepository.existsById(idInexistente)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> {
            carritoService.eliminarItem(idInexistente);
        });
    }

    @Test
    void testCalcularTotalCarrito_SumaCorrectamente() {
        Long clienteId = 1L;
        Carrito item1 = new Carrito();
        item1.setPrecioUnitario(new BigDecimal("1000"));
        item1.setCantidad(2);

        Carrito item2 = new Carrito();
        item2.setPrecioUnitario(new BigDecimal("500"));
        item2.setCantidad(1);

        when(carritoRepository.findByClienteId(clienteId)).thenReturn(List.of(item1, item2));

        BigDecimal total = carritoService.calcularTotalCarrito(clienteId);
        assertEquals(new BigDecimal("2500"), total);
    }
}