CREATE TABLE envios (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        pedido_id BIGINT NOT NULL,
                        direccion_destino VARCHAR(255) NOT NULL,
                        estado VARCHAR(50) NOT NULL,
                        codigo_seguimiento VARCHAR(100) UNIQUE
);