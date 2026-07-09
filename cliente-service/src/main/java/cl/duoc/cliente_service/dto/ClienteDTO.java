package cl.duoc.cliente_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteDTO {

    @Schema(description = "ID del cliente", example = "1")
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nombre completo del cliente", example = "Cristobal Pérez")
    private String nombre;

    @Email(message = "El formato del correo no es válido")
    @NotBlank(message = "El email es obligatorio")
    @Schema(description = "Correo electrónico del cliente", example = "cris.perez@duoc.cl")
    private String email;
}