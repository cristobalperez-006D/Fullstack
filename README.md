# 🚀 Proyecto Semestral: Arquitectura de Microservicios Distribuidos
**Asignatura:** Desarrollo FullStack 1 (DSY1103)  
**Institución:** Duoc UC  
**Evaluación:** Evaluación Parcial 3 - Encargo con Defensa Técnica  
**Docente:** Christian Acuña

---

## 👥 Integrantes del Equipo
* **Estudiante 1:** [Amaro Salazar]  
* **Estudiante 2:** [Angel Vega] 
* **Estudiante 3:** [Cristobal Pérez] 

---

## 📝 Descripción del Contexto / Dominio
*Seicinco Store** es una solución integral de comercio electrónico (E-Commerce) diseñada específicamente para la industria del *retail* de vestuario, calzado y accesorios de moda. 
La plataforma permite a los usuarios finales navegar por colecciones de temporada, gestionar un carrito de compras interactivo, realizar transacciones de pago seguras y dar seguimiento logístico a sus despachos. 

Debido a la alta estacionalidad del mercado de la moda y la necesidad de soportar picos masivos de concurrencia (tales como eventos CyberDay o lanzamientos de nuevas colecciones), el ecosistema completo ha sido diseñado bajo una **arquitectura distribuida basada en microservicios independientes**[cite: 1]. 
Esto garantiza que componentes críticos como el procesamiento de pagos o la actualización de stock puedan escalar de manera autónoma, aislando las fallas y manteniendo la resiliencia global de la aplicación.

---

## ⚙️ Arquitectura y Microservicios Implementados
El sistema está compuesto por una arquitectura distribuida que cuenta con **un API Gateway centralizado** y los siguientes **10 microservicios independientes**:

1. **api-gateway:** Centraliza el enrutamiento y aplica filtros de seguridad/tráfico.
2. **eureka-server (o Discovery Server):** Registro y descubrimiento de servicios dinámico.
3. **config-server:** Centralización de archivos de configuración `.yml`.
4. **servicio-usuarios:** Gestión de cuentas, perfiles y autenticación.
5. **servicio-productos:** Catálogo e inventario de productos.
6. **servicio-pedidos:** Procesamiento de órdenes de compra.
7. **servicio-pagos:** Integración con pasarelas de pago y estados financieros.
8. **servicio-notificaciones:** Envío de correos y alertas distribuidas.
9. **servicio-envios:** Despacho y seguimiento logístico.
10. **servicio-reportes:** Generación de métricas y analíticas de negocio.

---

## 🔀 Rutas Principales del API Gateway
Toda la comunicación externa se redirige a través del Gateway (Puerto base local: `8080`).

| Microservicio | Prefijo de Ruta | Endpoint de Ejemplo | Descripción |
| :--- | :--- | :--- | :--- |
| **Usuarios** | `/api/v1/usuarios/**` | `GET /api/v1/usuarios/1` | Obtener datos de un usuario |
| **Productos** | `/api/v1/productos/**` | `POST /api/v1/productos` | Registrar nuevo producto |
| **Pedidos** | `/api/v1/pedidos/**` | `GET /api/v1/pedidos/tracking` | Seguimiento de orden |

---

## 📄 Documentación de API (Swagger / OpenAPI)
Cada microservicio expone su interfaz interactiva a través de `springdoc-openapi`.

* **Acceso Local (Gateway):** `http://localhost:9000/swagger-ui.html`
* **Acceso Remoto (Producción):** `http://localhost:9090/swagger-ui.html`

> 💡 *Nota para la defensa:* Las especificaciones coinciden exactamente con los modelos JSON y códigos de respuesta HTTP (`200 OK`, `201 Created`, `400 BadRequest`, `404 NotFound`) configurados en los controladores.

---

## 🧪 Pruebas Unitarias y Cobertura (JUnit 5 + Mockito)
La lógica de negocio crítica en la capa `@Service` está completamente validada bajo la estructura **Given-When-Then**.

