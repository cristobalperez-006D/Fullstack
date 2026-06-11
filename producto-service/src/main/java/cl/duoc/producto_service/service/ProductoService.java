package cl.duoc.producto_service.service;

import cl.duoc.producto_service.dto.ProductoDTO;
import cl.duoc.producto_service.model.Producto;
import cl.duoc.producto_service.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<ProductoDTO> findAll() {
        return productoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO findById(Long id) {
        return productoRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public ProductoDTO save(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());

        Producto guardado = productoRepository.save(producto);
        return mapToDTO(guardado);
    }

    public ProductoDTO update(Long id, ProductoDTO dto) {
        return productoRepository.findById(id).map(p -> {
            p.setNombre(dto.getNombre());
            p.setPrecio(dto.getPrecio());
            Producto actualizado = productoRepository.save(p);
            return mapToDTO(actualizado);
        }).orElse(null);
    }

    public void delete(Long id) {
        productoRepository.deleteById(id);
    }

    private ProductoDTO mapToDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        return dto;
    }
}