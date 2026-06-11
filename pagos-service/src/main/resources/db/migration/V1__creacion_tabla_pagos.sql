CREATE TABLE pagos (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       pedido_id BIGINT NOT NULL,
                       cliente_id BIGINT NOT NULL,
                       monto DECIMAL(10,2) NOT NULL,
                       metodo_pago VARCHAR(50) NOT NULL,
                       estado VARCHAR(50) NOT NULL,
                       fecha_pago DATETIME NOT NULL
);