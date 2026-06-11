package cl.duoc.carrito_service.mapper;

import cl.duoc.carrito_service.dto.CarritoDTO;
import cl.duoc.carrito_service.model.Carrito;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CarritoMapper {
    public Carrito toEntity(CarritoDTO dto) {
        if (dto == null) return null;

        Carrito entity = new Carrito();
        entity.setClienteId(dto.getClienteId());
        entity.setProductoId(dto.getProductoId());
        entity.setCantidad(dto.getCantidad());
        entity.setPrecioUnitario(dto.getPrecioUnitario());
        return entity;
    }

    public CarritoDTO toDto(Carrito entity) {
        if (entity == null) return null;

        CarritoDTO dto = new CarritoDTO();
        dto.setId(entity.getId());
        dto.setClienteId(entity.getClienteId());
        dto.setProductoId(entity.getProductoId());
        dto.setCantidad(entity.getCantidad());
        dto.setPrecioUnitario(entity.getPrecioUnitario());

        if (entity.getPrecioUnitario() != null && entity.getCantidad() != null) {
            dto.setSubtotal(entity.getPrecioUnitario().multiply(BigDecimal.valueOf(entity.getCantidad())));
        }

        return dto;
    }
}
