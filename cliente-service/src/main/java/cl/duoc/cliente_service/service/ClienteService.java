package cl.duoc.cliente_service.service;

import cl.duoc.cliente_service.dto.ClienteDTO;
import cl.duoc.cliente_service.model.Cliente;
import cl.duoc.cliente_service.repository.ClienteRepository;
import cl.duoc.cliente_service.exception.RecursoNoEncontradoException; // Importa tu excepción
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
                // Si no existe, tiramos la excepción. ¡Adiós al null!
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un cliente con el ID: " + id));
    }

    public ClienteDTO save(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setEmail(dto.getEmail());

        Cliente guardado = clienteRepository.save(cliente);
        return mapToDTO(guardado);
    }

    public void delete(Long id) {
        // Validación extra: verificamos si existe antes de borrar
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Imposible eliminar: El cliente con ID " + id + " no existe.");
        }
        clienteRepository.deleteById(id);
    }

    public ClienteDTO update(Long id, ClienteDTO dto) {
        return clienteRepository.findById(id).map(c -> {
            c.setNombre(dto.getNombre());
            c.setEmail(dto.getEmail());
            Cliente actualizado = clienteRepository.save(c);
            return mapToDTO(actualizado);
        }).orElseThrow(() -> new RecursoNoEncontradoException("No se puede actualizar, el cliente ID " + id + " no se encuentra en el registro."));
    }

    private ClienteDTO mapToDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setEmail(cliente.getEmail());
        return dto;
    }
    public List<ClienteDTO> findByNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Long contarClientes() {
        return clienteRepository.count();
    }
}