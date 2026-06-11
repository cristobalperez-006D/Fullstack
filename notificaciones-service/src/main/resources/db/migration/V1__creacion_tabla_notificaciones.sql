CREATE TABLE notificaciones (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                cliente_id BIGINT NOT NULL,
                                tipo VARCHAR(50) NOT NULL,
                                mensaje VARCHAR(500) NOT NULL,
                                fecha_envio DATETIME NOT NULL
);