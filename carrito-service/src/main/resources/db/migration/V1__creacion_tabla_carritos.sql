CREATE TABLE carritos (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          cliente_id BIGINT NOT NULL,
                          producto_id BIGINT NOT NULL,
                          cantidad INT NOT NULL,
                          precio_unitario DECIMAL(10,2) NOT NULL
);