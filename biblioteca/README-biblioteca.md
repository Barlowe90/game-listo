# Microservicio Biblioteca – GameListo

Este documento describe el microservicio `biblioteca` del monorepo GameListo: responsabilidades, modelo de datos,
contratos HTTP (endpoints), convenciones de diseño y comandos de desarrollo y prueba. Está dirigido a desarrolladores
que integran, despliegan o mantienen el servicio.

Tabla de contenidos

- Descripción
- Responsabilidades y alcance
- Arquitectura y organización del código
- Modelo de datos (conceptual)
- Contratos HTTP (endpoints)
- Seguridad
- Desarrollo y ejecución local
- Pruebas
- Variables de configuración importantes
- Convenciones y estilo
- Mantenimiento y próximos pasos

Descripción

`biblioteca` se encarga de la gestión de colecciones personales de videojuegos y del registro del estado y la
puntuación de juegos por usuario. Internamente maneja entidades de referencia a usuarios y juegos, y expone un conjunto
claro de operaciones REST para que clientes y otros servicios consuman.

Responsabilidades y alcance

- CRUD de listas de usuario (crear, actualizar metadatos, eliminar, consultar).
- Gestión de elementos en una lista (añadir y quitar referencias a juegos).
- Registro y actualización del estado de un juego para un usuario (p. ej. 'jugando', 'completado') y su valoración.
- Persistencia de entidades relacionadas con listas, referencias a juegos y estados/puntuaciones.
- Contratos REST para integración con BFF o frontends, y puntos de extensión para integración con mensajería/eventos.

Arquitectura y organización del código

Se sigue una aproximación hexagonal / DDD con separación en tres capas principales:

- `domain` — Entidades de dominio, Value Objects y reglas de negocio puras.
- `application` — Casos de uso (use cases / handlers) que orquestan la lógica del dominio.
- `infrastructure` — Adaptadores: controladores REST, persistencia JPA (entities, mappers, repositories) y otros
  adaptadores técnicos.

Principios aplicados:

- Las entidades y VOs del dominio no dependen de Spring.
- Los repositorios se definen como puertos en dominio y se implementan en infraestructura.
- Los controladores son delgados: traducen DTOs → comandos → casos de uso → DTOs de salida.

Modelo de datos (conceptual)

- UsuarioRef: referencia ligera a usuario (id, username, avatar...).
- GameRef: referencia ligera a juego (id, nombre, cover, campos básicos).
- ListaGame: agregado raíz que contiene metadatos de la lista (id, nombre, tipo) y una colección de `ListaGameItem`.
- ListaGameItem: relación entre `ListaGame` y `GameRef` (PK compuesta mediante `ListaGameItemId`).
- GameEstado: entidad que almacena el estado (`enum`) y la puntuación del juego para un usuario.

Las implementaciones JPA, mappers y repositorios se encuentran bajo `infrastructure/out/persistence`.

Contratos HTTP (endpoints)

Base path: `/v1/biblioteca`

Listas

- POST `/lists`
    - Request: `CrearListaGameRequest` — { "nombre": "string", "tipo": "string" }
    - Response: `ListaGameResponse` — { "id": "uuid", "usuarioRefId": "uuid", "nombre": "string", "tipo": "string" }
    - Código: 201 Created

- PATCH `/lists/{listId}`
    - Request: `EditarListaGameRequest` — { "nombre": "string" }
    - Response: `ListaGameResponse`
    - Código: 200 OK

- DELETE `/lists/{listId}`
    - Código: 204 No Content

- GET `/lists/{listId}`
    - Response: `ListaGameResponse`
    - Código: 200 OK

- GET `/lists`
    - Response: lista de `ListaGameResponse`
    - Código: 200 OK

Gestión de juegos en listas

- POST `/lists/{listaId}/games/{gameRefId}`
    - Añade una referencia de juego a la lista.
    - Código: 200 (No Content)

- DELETE `/lists/{listaId}/games/{gameRefId}`
    - Elimina una referencia de juego de la lista.
    - Código: 200 (No Content)

Estado y valoración de juegos

- POST `/games/{gameRefId}/state`
    - Request: `CrearGameEstadoRequest` — { "estado": "string" }
    - Código: 200 OK

- POST `/games/{gameRefId}/rate`
    - Request: `RateGameEstadoRequest` — { "rating": number }
    - Código: 200 OK

Seguridad

- Los controladores usan `@PreAuthorize` para validar que la petición provenga del usuario correcto o de un rol
  administrativo. El mecanismo concreto de autenticación/autorización se define en la configuración global de
  seguridad del proyecto.
- Recomendación: validar tokens en el borde (API Gateway) y propagar `X-User-*` headers a microservicios internos.

Desarrollo y ejecución local

Requisitos mínimos:

- JDK 21
- Maven (se usa el wrapper incluido)

Comandos útiles (PowerShell en Windows, ejecutar desde la carpeta `biblioteca`):

```powershell
# Ejecutar la aplicación
.\mvnw.cmd spring-boot:run;

# Ejecutar tests
.\mvnw.cmd test;
```

La configuración de datasource y JPA se controla mediante propiedades de Spring (ver `src/main/resources` y
`src/test/resources`).

Pruebas

- La base contiene tests unitarios y de integración. Los resultados de ejecución se encuentran en
  `target/surefire-reports`.
- Strategy: las pruebas de dominio son unitarias sin dependencias de Spring; las pruebas de integración usan H2
  (configurable) para endpoints y repositorios.

Variables de configuración importantes

- `spring.datasource.url` — URL de conexión a base de datos
- `spring.jpa.hibernate.ddl-auto` — estrategia DDL (p. ej. `update` o `validate`)
- Otras propiedades de logging y perfiles de Spring pueden encontrarse en `src/main/resources`.

Convenciones y estilo

- Paquetes: `domain`, `application`, `infrastructure`.
- Repositorios: interfaces con sufijo `Repositorio` ubicadas en la capa de dominio (puertos), implementaciones en
  `infrastructure/out/persistence`.
- Value Objects inmutables con fábrica `of()` cuando procede.
- Controladores: traducción mínima entre DTOs y casos de uso. No incluir lógica de negocio en controladores.

Mantenimiento y próximos pasos

- Revisar y documentar contratos JSON concretos para cada endpoint si se van a exponer a consumidores externos.
- Considerar agregar OpenAPI/Swagger para facilitar la integración.
- Si se requiere flujo de eventos (por ej. reindexación o notificaciones), conectar adaptadores de mensajería en
  `infrastructure/messaging` y documentar el contrato de eventos.

Contribuir

- Abrir PRs con cambios pequeños y focused commits. Añadir pruebas relevantes para cualquier comportamiento nuevo.
- Mantener la consistencia con las convenciones del repositorio raíz.

Referencias rápidas

- Código fuente: `src/main/java/com/gamelisto/biblioteca`
- Tests: `src/test/java`

Licencia

El proyecto hereda la licencia del repositorio raíz.
