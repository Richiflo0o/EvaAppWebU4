# Cuestionario — Parte A del examen de la Unidad IV

> **Cómo se llena este archivo.** Responda **dentro de este mismo archivo**, debajo de cada pregunta, en el bloque marcado como `**Respuesta:**`. No borre ni reescriba los enunciados: el evaluador compara pregunta por pregunta. No añada ni quite secciones.
>
> **Este archivo se versiona en el repositorio.** Debe existir en la raíz, llamarse exactamente `Cuestionario.md`, y sus respuestas deben llegar por *commits* sucesivos hechos cuando el docente lo indique. Un archivo que aparece completo en un único *commit* al final de la sesión no cumple el protocolo y se trata según el criterio de piso 4 del examen.
>
> Se valora la precisión técnica y la justificación, **no la extensión**. Una respuesta correcta de seis líneas vale más que una página imprecisa. Cuando la pregunta pida referirse al proyecto base, hágalo con nombres concretos de clases o de *endpoints*.

---

## Datos del estudiante

| Campo | Valor |
|---|---|
| Apellidos y nombres | Velez Lopez Ricardo Elias|
| Número de carnet | 1316400942 |
| Correo institucional | rvelezl3@uteq.edu.ec|
| Fecha | 28/08/2026 |
| URL del repositorio | https://github.com/Richiflo0o/EvaAppWebU4 |

---

## A1. Restricciones de REST aplicadas a un caso concreto — 8 puntos

**a) Enuncie las seis restricciones del estilo arquitectónico REST según Fielding. (3 puntos)**

**Respuesta:**

**1. Client-Server**

**2. Stateless**

**3. Cache**

**4. Uniform Interface**

**5. Layered System**

**6. Code-On-Demand**


**b) El proyecto base expone `GET /api/v1/autores` y guarda el estado de la sesión del usuario solo en el JWT que el cliente envía en cada petición. Explique qué restricción concreta se está cumpliendo con esa decisión y qué consecuencia práctica tiene para escalar el sistema a varios servidores detrás de un balanceador. (3 puntos)**

**Respuesta:**

**El proyecto al guardar el estado solo en el JWT cumple Stateless. Esto significa que cada petición es independiente y no depende de sesiones almacenadas en servidor. Para escalar a varios servidores detrás de un balanceador, cualquier servidor puede procesar cualquier petición porque el estado viaja en el token, no en memoria. No hay sesión que sincronizar entre servidores**

**c) De las seis restricciones, indique cuál es opcional y dé un ejemplo real de una API que la use. (2 puntos)**

**Respuesta:**

**WebExtensions de navegador donde el servidor envía JavaScript que el cliente ejecuta**


---

## A2. Anatomía y ciclo de vida de un JWT — 8 puntos

**a) Un JWT tiene tres partes separadas por puntos. Nómbrelas en orden e indique qué contiene cada una. (3 puntos)**

**Respuesta:**

**Header, Payload, Signature**

**b) Un compañero afirma: «como el JWT va firmado, puedo guardar en el *payload* la contraseña del usuario sin riesgo». Explique por qué está equivocado, precisando la diferencia entre firmar y cifrar. (2 puntos)**

**Respuesta:**

**Firmar NO es cifrar. La firma garantiza integridad (no fue modificado), pero el payload se puede leer sin la clave. Cualquiera que decodifique un JWT (base64) ve el contenido. Guardar la contraseña expone el secreto a cualquiera intercepte el token**

**c) El JWT es *stateless* por diseño, lo que genera un problema conocido: no se puede invalidar un token antes de que expire. Describa dos estrategias distintas para revocarlo y señale la desventaja de cada una. (3 puntos)**

**Respuesta:**

**Blacklist en Redis: Almacena tokens revocados. Desventaja: requiere consultas a Redis (latencia)**

**Tiempo de expiración corto + refresh tokens: Desventaja:增加了 complejidad del cliente para manejar la renovación**

---

## A3. SOAP frente a REST — 8 puntos

**a) Complete la tabla comparativa con seis criterios entre SOAP y REST. (5 puntos)**

**Respuesta:**

| Criterio | SOAP | REST |
|---|---|---|
| Formato del mensaje |XML envelope con header/body |JSON, XML, XMLText, etc. |
| Contrato de descripción |WSDL | OpenAPI/Swagger|
| Sobrecarga de serialización |XML namespaces, SOAP envelope|JSON simple, menos overhead |
| Tipado |Fuerte |Débil |
| Facilidad de consumo desde un cliente móvil |Complejo, pesado | Ligero, fácil integración|
| Manejo de errores |Fault codes SOAP |Códigos HTTP estándar|

**b) El Servicio de Rentas Internas del Ecuador expone la autorización de comprobantes electrónicos mediante servicios SOAP. Explique dos razones técnicas por las que una institución de ese tipo mantiene SOAP en lugar de migrar a REST. (3 puntos)**

**Respuesta:**

**Seguridad y cumplimiento normativo: SOAP tiene WS-Security para firmas digitales y cifrado de extremo a extremo, requerido para comprobantes fiscales**

**Transacciones distribuidas: SOAP soporta WS-Transactions para garantizar atomicidad en operaciones críticas**

---

## A4. Cache-aside sobre un servicio externo — 8 puntos

> El proyecto base define en `CacheConfig` dos espacios de caché: `libros` con TTL de 2 minutos y `openlibrary` con TTL de 24 horas.

**a) Describa el patrón *cache-aside* en sus cuatro pasos, desde que llega la petición hasta que se responde. (3 puntos)**

**Respuesta:**

**Paso 1: Llega la petición, buscar la clave en el cache**

**Paso 2: Si está en cache → retornar respuesta cacheada**

**Paso 3: Si no está → llamar al servicio externo**

**paso 4: Almacenar el resultado en el cache con TTL y retornar**

**b) Justifique técnicamente por qué el TTL de `openlibrary` es doce veces mayor que el de `libros`, y qué criterio general debe guiar la elección de un TTL. (3 puntos)**

**Respuesta:**



**c) Explique por qué nunca debe almacenarse en caché la respuesta de un fallo del servicio externo, y describa qué le ocurriría al sistema si se hiciera. (2 puntos)**

**Respuesta:**



---

## A5. Diagnóstico de códigos de estado y contrato de errores — 8 puntos

> Todos los errores del proyecto base salen en formato *Problem Details* conforme a la RFC 9457, que obsoleta a la RFC 7807.

Para cada escenario indique el código HTTP correcto y explique en una línea por qué. **Cada fila vale 1 punto** (0,5 por el código y 0,5 por la justificación); el literal g) vale 2 puntos.

| # | Escenario | Código | Justificación (una línea) |
|---|---|---|---|
| a | `GET /api/v1/libros/999999` y ese identificador no existe | | |
| b | `POST /api/v1/libros` sin cabecera `Authorization` | | |
| c | Usuario autenticado con rol `LECTOR` envía `POST /api/v1/libros` | | |
| d | `POST /api/v1/libros` con el campo `titulo` vacío | | |
| e | Prestar un libro a un socio que ya tiene tres préstamos activos | | |
| f | La API de Open Library no responde dentro del *timeout* configurado | | |

**g) Explique por qué devolver `200 OK` con un cuerpo `{"success": false}` es un error de diseño, y qué restricción de REST se incumple al hacerlo. (2 puntos)**

**Respuesta:**



---

## Declaración de honestidad académica

Marque con una `x` y complete:

- [ ] Declaro que estas respuestas son de mi autoría, redactadas durante la sesión de examen, sin asistencia de inteligencia artificial ni comunicación con terceros.

Firma (nombre completo): ______________________________
