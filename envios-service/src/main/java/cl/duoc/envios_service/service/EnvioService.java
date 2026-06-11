package cl.duoc.envios_service.service;
import cl.duoc.envios_service.dto.EnvioDTO;
import cl.duoc.envios_service.model.Envio;
import cl.duoc.envios_service.repository.EnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    public List<EnvioDTO> findAll() {
        return envioRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public EnvioDTO findById(Long id) {
        return envioRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public EnvioDTO findByPedidoId(Long pedidoId) {
        Envio envio = envioRepository.findByPedidoId(pedidoId);
        return envio != null ? mapToDTO(envio) : null;
    }

    public EnvioDTO save(EnvioDTO dto) {
        Envio envio = new Envio();
        envio.setPedidoId(dto.getPedidoId());
        envio.setDireccionDestino(dto.getDireccionDestino());
        // Si no mandan estado, le chantamos PENDIENTE por defecto
        envio.setEstado(dto.getEstado() != null ? dto.getEstado() : "PENDIENTE");
        envio.setCodigoSeguimiento(dto.getCodigoSeguimiento());

        Envio guardado = envioRepository.save(envio);
        return mapToDTO(guardado);
    }

    // Un método pulento solo para actualizar el estado del envío rápido
    public EnvioDTO updateEstado(Long id, String nuevoEstado) {
        return envioRepository.findById(id).map(e -> {
            e.setEstado(nuevoEstado);
            Envio actualizado = envioRepository.save(e);
            return mapToDTO(actualizado);
        }).orElse(null);
    }

    public void delete(Long id) {
        envioRepository.deleteById(id);
    }

    // Tu clásico mapeador a mano
    private EnvioDTO mapToDTO(Envio envio) {
        EnvioDTO dto = new EnvioDTO();
        dto.setId(envio.getId());
        dto.setPedidoId(envio.getPedidoId());
        dto.setDireccionDestino(envio.getDireccionDestino());
        dto.setEstado(envio.getEstado());
        dto.setCodigoSeguimiento(envio.getCodigoSeguimiento());
        return dto;
    }
}
