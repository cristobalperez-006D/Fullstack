package cl.duoc.cliente_service.controller;

import cl.duoc.cliente_service.dto.ClienteDTO;
import cl.duoc.cliente_service.dto.ApiResponseDTO; // ¡Importante!
import cl.duoc.cliente_service.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Administración del repositorio de usuarios")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Operation(summary = "Extraer directorio de clientes")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<ClienteDTO>>> obtenerTodos() {
        List<ClienteDTO> data = clienteService.findAll();
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Directorio recuperado", data));
    }

    @Operation(summary = "Recuperar perfil por ID")
    @ApiResponse(responseCode = "200", description = "Perfil encontrado")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<ClienteDTO>> obtenerPorId(@PathVariable Long id) {
        ClienteDTO data = clienteService.findById(id);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Perfil encontrado", data));
    }

    @Operation(summary = "Aprovisionar nuevo cliente")
    @ApiResponse(responseCode = "201", description = "Cliente integrado")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<ClienteDTO>> crear(@Valid @RequestBody ClienteDTO dto) {
        ClienteDTO data = clienteService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDTO<>(HttpStatus.CREATED.value(), "Cliente registrado", data));
    }

    @Operation(summary = "Actualizar perfil")
    @ApiResponse(responseCode = "200", description = "Perfil actualizado")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<ClienteDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        ClienteDTO data = clienteService.update(id, dto);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Perfil actualizado", data));
    }

    @Operation(summary = "Baja de cliente")
    @ApiResponse(responseCode = "204", description = "Perfil eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Buscar clientes por nombre")
    @GetMapping(value = "/search/{nombre}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<List<ClienteDTO>>> buscarPorNombre(@PathVariable String nombre) {
        List<ClienteDTO> data = clienteService.findByNombre(nombre);
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Búsqueda exitosa", data));
    }

    @Operation(summary = "Obtener conteo total de clientes")
    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<Long>> contar() {
        Long total = clienteService.contarClientes();
        return ResponseEntity.ok(new ApiResponseDTO<>(HttpStatus.OK.value(), "Conteo exitoso", total));
    }
}