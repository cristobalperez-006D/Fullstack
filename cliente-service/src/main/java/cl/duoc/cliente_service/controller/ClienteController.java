package cl.duoc.cliente_service.controller;

import cl.duoc.cliente_service.dto.ClienteDTO;
import cl.duoc.cliente_service.dto.ApiResponseDTO; // ¡Importante!
import cl.duoc.cliente_service.service.ClienteService;
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
@RequestMapping("/api/v1/clientes")
@Tag(name = "Gestión de Identidad de Clientes", description = "Servicio core para la administración y consulta del repositorio de usuarios.")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Operation(summary = "Extraer directorio de clientes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Directorio de clientes recuperado satisfactoriamente.")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ClienteDTO>>> obtenerTodos() {
        List<ClienteDTO> data = clienteService.findAll();
        String mensaje = "Directorio completo de clientes recuperado.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Recuperar perfil de cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado."),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ClienteDTO>> obtenerPorId(@PathVariable Long id) {
        ClienteDTO data = clienteService.findById(id);
        String mensaje = "Perfil del cliente extraído con precisión.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Aprovisionar nuevo cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente integrado al sistema.")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ClienteDTO>> crear(@RequestBody ClienteDTO dto) {
        ClienteDTO data = clienteService.save(dto);
        String mensaje = "Nuevo perfil de cliente aprovisionado exitosamente.";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), mensaje, data));
    }

    @Operation(summary = "Actualizar datos maestros del perfil")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ClienteDTO>> actualizar(@PathVariable Long id, @RequestBody ClienteDTO dto) {
        ClienteDTO data = clienteService.update(id, dto);
        String mensaje = "Datos maestros del perfil sincronizados correctamente.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, data));
    }

    @Operation(summary = "Baja de cliente en el sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil eliminado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> eliminar(@PathVariable Long id) {
        clienteService.delete(id);
        String mensaje = "El perfil del cliente ha sido purgado del repositorio.";
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), mensaje, null));
    }
}