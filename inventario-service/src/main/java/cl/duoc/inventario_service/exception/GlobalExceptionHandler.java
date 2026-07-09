package cl.duoc.inventario_service.exception;

import cl.duoc.inventario_service.dto.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Si lanzas RecursoNoEncontradoException, devolvemos un 404
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleNotFound(RecursoNoEncontradoException ex) {
        ApiResponseDTO<Void> response = new ApiResponseDTO<>(
                HttpStatus.NOT_FOUND.value(),
                "Error de búsqueda: " + ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Si falla Feign o cualquier otra cosa, devolvemos un 500 elegante
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGeneralError(Exception ex) {
        ApiResponseDTO<Void> response = new ApiResponseDTO<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Anomalía detectada en el servicio: " + ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        // Recorremos los errores de validación de los campos
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        ApiResponseDTO<Map<String, String>> response = new ApiResponseDTO<>(
                HttpStatus.BAD_REQUEST.value(),
                "Error de validación: Verifique los campos enviados",
                errores
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}