CREATE TABLE pedidos (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         cliente_id BIGINT NOT NULL,
                         monto_total DECIMAL(10,2) NOT NULL,
                         estado VARCHAR(50) NOT NULL,
                         fecha_pedido DATETIME NOT NULL
);