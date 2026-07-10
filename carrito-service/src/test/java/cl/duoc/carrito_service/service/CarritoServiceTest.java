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

    // --- TEST 1 ---
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

    // --- TEST 2 ---
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

    // --- TEST 3 ---
    @Test
    void testEliminarItem_DebeLanzarExcepcion_CuandoNoExiste() {
        Long idInexistente = 999L;
        when(carritoRepository.existsById(idInexistente)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> {
            carritoService.eliminarItem(idInexistente);
        });
    }

    // --- TEST 4 ---
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

    // --- TEST 5 (NUEVO) ---
    @Test
    void testEliminarItem_Exito() {
        Long idItem = 1L;
        when(carritoRepository.existsById(idItem)).thenReturn(true);
        doNothing().when(carritoRepository).deleteById(idItem);

        assertDoesNotThrow(() -> carritoService.eliminarItem(idItem));
        verify(carritoRepository, times(1)).deleteById(idItem);
    }

    // --- TEST 6 (NUEVO) ---
    @Test
    void testCalcularTotalCarrito_CarritoVacio() {
        Long clienteId = 1L;
        when(carritoRepository.findByClienteId(clienteId)).thenReturn(Collections.emptyList());

        BigDecimal total = carritoService.calcularTotalCarrito(clienteId);

        // Si no hay items, el total debería ser cero
        assertEquals(BigDecimal.ZERO, total);
    }

    // --- TEST 7 (NUEVO) ---
    @Test
    void testAgregarItem_ClienteNoEncontrado_LanzaExcepcion() {
        CarritoDTO dto = new CarritoDTO();
        dto.setClienteId(999L);
        dto.setProductoId(10L);

        // Usamos lenient() para que Mockito no se ponga pesado si el stub no se alcanza a usar
        lenient().when(clienteFeignClient.obtenerClienteRaw(999L))
                .thenThrow(new RuntimeException("Cliente no encontrado"));

        assertThrows(RuntimeException.class, () -> {
            carritoService.agregarItem(dto);
        });
    }
    // --- TEST 8 (NUEVO) ---
    @Test
    void testAgregarItem_ProductoNoEncontrado_LanzaExcepcion() {
        CarritoDTO dto = new CarritoDTO();
        dto.setClienteId(1L);
        dto.setProductoId(999L);

        // Usamos lenient() en los dos mockeos para que Mockito no llore
        lenient().when(clienteFeignClient.obtenerClienteRaw(1L)).thenReturn(crearMockResponse());
        lenient().when(productoFeignClient.obtenerProductoRaw(999L))
                .thenThrow(new RuntimeException("Producto no encontrado"));

        assertThrows(RuntimeException.class, () -> {
            carritoService.agregarItem(dto);
        });
    }
}