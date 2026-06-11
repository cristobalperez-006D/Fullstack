package cl.duoc.cliente_service.service;

import cl.duoc.cliente_service.dto.ClienteDTO;
import cl.duoc.cliente_service.model.Cliente;
import cl.duoc.cliente_service.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<ClienteDTO> findAll() {
        return clienteRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ClienteDTO findById(Long id) {
        return clienteRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public ClienteDTO save(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setEmail(dto.getEmail());

        Cliente guardado = clienteRepository.save(cliente);
        return mapToDTO(guardado);
    }

    public void delete(Long id) {
        clienteRepository.deleteById(id);
    }

    public ClienteDTO update(Long id, ClienteDTO dto) {
        return clienteRepository.findById(id).map(c -> {
            c.setNombre(dto.getNombre());
            c.setEmail(dto.getEmail());
            Cliente actualizado = clienteRepository.save(c);
            return mapToDTO(actualizado);
        }).orElse(null);
    }

    // Tu método mapeador clásico pa' no usar librerías raras
    private ClienteDTO mapToDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setEmail(cliente.getEmail());
        return dto;
    }
}