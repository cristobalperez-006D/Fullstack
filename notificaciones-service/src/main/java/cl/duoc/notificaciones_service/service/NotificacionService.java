package cl.duoc.notificaciones_service.service;

import cl.duoc.notificaciones_service.dto.NotificacionDTO;
import cl.duoc.notificaciones_service.model.Notificacion;
import cl.duoc.notificaciones_service.repository.NotificacionRepository;
import cl.duoc.notificaciones_service.exception.RecursoNoEncontradoException; // ¡No olvides importar esto!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    public List<NotificacionDTO> findAll() {
        return notificacionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificacionDTO> findByClienteId(Long clienteId) {
        List<Notificacion> notificaciones = notificacionRepository.findByClienteId(clienteId);
        // Si no tiene notificaciones, lanzamos la excepción para el 404
        if (notificaciones.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron notificaciones para el cliente ID: " + clienteId);
        }
        return notificaciones.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public NotificacionDTO save(NotificacionDTO dto) {
        Notificacion notificacion = new Notificacion();
        notificacion.setClienteId(dto.getClienteId());
        notificacion.setTipo(dto.getTipo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setFechaEnvio(dto.getFechaEnvio() != null ? dto.getFechaEnvio() : LocalDateTime.now());

        Notificacion guardada = notificacionRepository.save(notificacion);
        return mapToDTO(guardada);
    }

    public void delete(Long id) {
        if (!notificacionRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No se pudo eliminar: Notificación ID " + id + " no encontrada.");
        }
        notificacionRepository.deleteById(id);
    }

    private NotificacionDTO mapToDTO(Notificacion notificacion) {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(notificacion.getId());
        dto.setClienteId(notificacion.getClienteId());
        dto.setTipo(notificacion.getTipo());
        dto.setMensaje(notificacion.getMensaje());
        dto.setFechaEnvio(notificacion.getFechaEnvio());
        return dto;
    }
}