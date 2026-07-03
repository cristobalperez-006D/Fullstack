package cl.duoc.inventario_service.dto;

import java.time.LocalDateTime;

public class ApiResponseDTO<T> {
    private int status;
    private String mensaje;
    private LocalDateTime timestamp;
    private T data;

    public ApiResponseDTO(int status, String mensaje, T data) {
        this.status = status;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
        this.data = data;
    }

    // Getters y Setters o usa @Data de Lombok
    public int getStatus() { return status; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public T getData() { return data; }
}