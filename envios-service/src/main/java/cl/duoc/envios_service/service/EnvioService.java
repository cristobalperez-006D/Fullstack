package cl.duoc.envios_service.service;

import cl.duoc.envios_service.dto.EnvioDTO;
import cl.duoc.envios_service.model.Envio;
import cl.duoc.envios_service.repository.EnvioRepository;
import cl.duoc.envios_service.exception.RecursoNoEncontradoException;
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
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Envío no encontrado con ID: " + id));
        return mapToDTO(envio);
    }

    public EnvioDTO findByPedidoId(Long pedidoId) {
        Envio envio = envioRepository.findByPedidoId(pedidoId);
        if (envio == null) {
            throw new RecursoNoEncontradoException("No existe registro de envío para el pedido ID: " + pedidoId);
        }
        return mapToDTO(envio);
    }

    public EnvioDTO save(EnvioDTO dto) {
        Envio envio = new Envio();
        envio.setPedidoId(dto.getPedidoId());
        envio.setDireccionDestino(dto.getDireccionDestino());
        envio.setEstado(dto.getEstado() != null ? dto.getEstado() : "PENDIENTE");
        envio.setCodigoSeguimiento(dto.getCodigoSeguimiento());

        Envio guardado = envioRepository.save(envio);
        return mapToDTO(guardado);
    }

    public EnvioDTO updateEstado(Long id, String estado) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Envío no encontrado con ID: " + id));
        envio.setEstado(estado);
        return mapToDTO(envioRepository.save(envio));
    }

    public void delete(Long id) {
        if (!envioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Imposible borrar: Envío ID " + id + " no encontrado.");
        }
        envioRepository.deleteById(id);
    }

    private EnvioDTO mapToDTO(Envio envio) {
        EnvioDTO dto = new EnvioDTO();
        dto.setId(envio.getId());
        dto.setPedidoId(envio.getPedidoId());
        dto.setDireccionDestino(envio.getDireccionDestino());
        dto.setEstado(envio.getEstado());
        dto.setCodigoSeguimiento(envio.getCodigoSeguimiento());
        return dto;
    }
    public Long contarPorEstado(String estado) {
        return envioRepository.findAll().stream()
                .filter(e -> e.getEstado().equalsIgnoreCase(estado))
                .count();
    }
}