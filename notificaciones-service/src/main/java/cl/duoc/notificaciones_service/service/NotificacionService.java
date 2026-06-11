package cl.duoc.notificaciones_service.service;
import cl.duoc.notificaciones_service.dto.NotificacionDTO;
import cl.duoc.notificaciones_service.model.Notificacion;
import cl.duoc.notificaciones_service.repository.NotificacionRepository;
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
        return notificacionRepository.findByClienteId(clienteId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public NotificacionDTO save(NotificacionDTO dto) {
        Notificacion notificacion = new Notificacion();
        notificacion.setClienteId(dto.getClienteId());
        notificacion.setTipo(dto.getTipo());
        notificacion.setMensaje(dto.getMensaje());

        // Le ponemos la hora actual si no viene en el DTO
        notificacion.setFechaEnvio(dto.getFechaEnvio() != null ? dto.getFechaEnvio() : LocalDateTime.now());

        Notificacion guardada = notificacionRepository.save(notificacion);
        return mapToDTO(guardada);
    }

    public void delete(Long id) {
        notificacionRepository.deleteById(id);
    }

    // Tu método regalón de mapeo
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
