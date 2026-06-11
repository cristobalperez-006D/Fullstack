INSERT INTO pagos (pedido_id, cliente_id, monto, metodo_pago, estado, fecha_pago)
VALUES (100, 1, 7990.00, 'WEBPAY', 'APROBADO', NOW());

INSERT INTO pagos (pedido_id, cliente_id, monto, metodo_pago, estado, fecha_pago)
VALUES (101, 2, 15500.50, 'TRANSFERENCIA', 'PENDIENTE', NOW());