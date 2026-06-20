package cl.duoc.pedidos_service.controller;

import cl.duoc.pedidos_service.dto.PedidoDTO;
import cl.duoc.pedidos_service.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Gestión de Pedidos", description = "API centralizada para la orquestación y gestión integral del ciclo de vida de las órdenes de compra. Provee un conjunto de operaciones transaccionales para administrar pedidos, consultar estados logísticos y auditar el historial de compras de los clientes.")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Operation(
            summary = "Recuperar el catálogo histórico de pedidos",
            description = "Despliega una lista exhaustiva con todos los pedidos (activos e históricos) registrados en el ecosistema. Este endpoint está optimizado para alimentar dashboards gerenciales y sistemas de auditoría interna."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Extracción de datos exitosa. Se retorna la colección completa de pedidos.")
    })
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> obtenerTodos() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @Operation(
            summary = "Inspeccionar un pedido específico por su ID",
            description = "Realiza una búsqueda de alta precisión en la base de datos para extraer los detalles estructurados de una orden de compra en particular, validando su existencia en tiempo real."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido localizado y retornado con su metadata intacta."),
            @ApiResponse(responseCode = "404", description = "El ID proporcionado no coincide con ningún registro válido en el sistema.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtenerPorId(
            @Parameter(description = "Identificador único y autogenerado del pedido", example = "1050")
            @PathVariable Long id
    ) {
        PedidoDTO pedido = pedidoService.findById(id);
        return pedido != null ? ResponseEntity.ok(pedido) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Listar el historial de compras de un cliente",
            description = "Filtra y compila todas las transacciones y pedidos asociados al perfil de un cliente específico. Fundamental para el módulo de atención al usuario y programas de fidelización."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial del cliente recuperado exitosamente.")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoDTO>> obtenerPorCliente(
            @Parameter(description = "Identificador único del cliente en el Customer Service", example = "42")
            @PathVariable Long clienteId
    ) {
        return ResponseEntity.ok(pedidoService.findByClienteId(clienteId));
    }

    @Operation(
            summary = "Emitir y registrar una nueva orden de compra",
            description = "Procesa un payload con la estructura del carrito, validando la integridad de los datos para generar un nuevo pedido formal en el sistema. Gatilla la fase inicial del workflow de logística."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "El pedido fue creado y persistido con éxito en la base de datos."),
            @ApiResponse(responseCode = "400", description = "Estructura del payload inválida o datos faltantes.")
    })
    @PostMapping
    public ResponseEntity<PedidoDTO> crear(
            @Parameter(description = "Objeto de transferencia de datos con la información del nuevo pedido")
            @RequestBody PedidoDTO dto
    ) {
        PedidoDTO creado = pedidoService.crearPedido(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Transicionar el estado logístico de un pedido",
            description = "Modifica el status actual de una orden (ej: de 'PENDIENTE' a 'EN_TRANSITO' o 'ENTREGADO'). Esta operación es crítica para mantener la trazabilidad en tiempo real del flujo de la cadena de suministro."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "El estado del pedido fue actualizado y sincronizado correctamente."),
            @ApiResponse(responseCode = "404", description = "No se pudo actualizar porque el pedido no existe en los registros.")
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoDTO> cambiarEstado(
            @Parameter(description = "ID del pedido a transicionar", example = "1050")
            @PathVariable Long id,
            @Parameter(description = "Nuevo status logístico a aplicar", example = "DESPACHADO")
            @RequestParam String estado
    ) {
        PedidoDTO actualizado = pedidoService.actualizarEstado(id, estado);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Purgar un pedido del sistema",
            description = "Ejecuta la eliminación física/lógica de un registro de pedido. Se debe usar con precaución, idealmente solo por perfiles de administrador para corregir anomalías."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "El registro fue aniquilado de la base de datos exitosamente.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del pedido que será eliminado de la faz de la tierra", example = "1050")
            @PathVariable Long id
    ) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}