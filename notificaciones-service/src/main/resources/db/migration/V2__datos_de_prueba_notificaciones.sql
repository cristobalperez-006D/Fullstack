INSERT INTO notificaciones (cliente_id, tipo, mensaje, fecha_envio)
VALUES (1, 'EMAIL', '¡Bienvenido a nuestra tienda! Tu cuenta ha sido creada exitosamente.', NOW());

INSERT INTO notificaciones (cliente_id, tipo, mensaje, fecha_envio)
VALUES (1, 'SMS', 'Tu pedido #1234 está en camino. Atento al repartidor.', NOW());

INSERT INTO notificaciones (cliente_id, tipo, mensaje, fecha_envio)
VALUES (2, 'EMAIL', 'Hemos recibido el pago de tu compra. ¡Gracias por preferirnos!', NOW());