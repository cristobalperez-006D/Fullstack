package cl.duoc.pagos_service.exception;

import cl.duoc.pagos_service.dto.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}