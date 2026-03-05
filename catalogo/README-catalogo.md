# Microservicio Catálogo

## Resumen

El microservicio *Catálogo* gestiona la información canónica de videojuegos. Está diseñado siguiendo principios de
arquitectura hexagonal y DDD: la lógica de dominio está separada de adaptadores de persistencia y de la capa de
exposición.

- Lenguaje: Java 21
- Framework: Spring Boot (módulo catalogo)
- Patrón: Hexagonal (domain / application / infrastructure)

## Modelo de dominio

Se mantiene una separación clara entre los datos estructurados de un juego (consultas frecuentes) y su contenido
enriquecido (multimedia):

### Game (núcleo estructurado)

`Game` es la entidad raíz (aggregate root) con la información estructurada y de consulta habitual. Contiene, entre
otros, los siguientes campos:

- id
- name
- summary
- coverUrl
- alternativeNames
- platforms
- gameType
- gameStatus
- dlcs
- expandedGames
- expansionIds
- externalGames
- franchises
- gameModes
- genres
- involvedCompanies
- keywords
- multiplayerModeIds
- parentGameId
- playerPerspectives
- remakeIds
- remasterIds
- similarGames
- themes

`Game` es la fuente de verdad para `coverUrl` y `alternativeNames`.

### GameDetail (contenido enriquecido)

`GameDetail` contiene los datos voluminosos o enriquecidos que no forman parte del núcleo estructurado:

- screenshots (List<String>)
- videos (List<String>)

`GameDetail` se refiere a `Game` mediante el identificador del juego y sirve para almacenar y consultar el contenido
multimedia de forma desacoplada.

## Persistencia

- PostgreSQL: almacena la entidad `Game` y sus colecciones estructuradas (tablas y element collections). Se usa para
  datos transaccionales y estructurados.
- MongoDB: almacena `GameDetail` (documentos con screenshots y videos). Se usa para contenido flexible y de alto
  volumen.

## Flujo de ingestión y sincronización

La sincronización desde proveedores externos (p. ej. IGDB) sigue este flujo:

1. Se obtienen los datos externos en un DTO (`IgdbGameDTO`).
2. Se crea/actualiza la entidad `Game` (Postgres) con los campos estructurados y con `alternativeNames` y `coverUrl`.
3. Se crea/actualiza el `GameDetail` (Mongo) con `screenshots` y `videos`.
4. Se publican eventos de dominio cuando procede (por ejemplo `GameCreado`) para notificar a otros servicios.

## API pública del servicio

El contrato público del microservicio expone endpoints REST (puerta de entrada bajo `/v1/catalogo`):

- GET /v1/catalogo/games/{id}
    - Devuelve los datos estructurados del juego (`GameResponse`) almacenados en PostgreSQL.
    - Contiene, entre otros, `id`, `name`, `summary`, `coverUrl`, `alternativeNames`, `platforms`, y metadatos.

- GET /v1/catalogo/games/{id}/detail
    - Devuelve los datos enriquecidos (`GameDetailResponse`) almacenados en MongoDB: `screenshots` y `videos`.

- POST /v1/catalogo/sync/games
    - Endpoint para disparar sincronizaciones/integraciones con la fuente externa (por ejemplo IGDB). El proceso
      persiste `Game` y `GameDetail` y publica eventos.

La capa BFF o la fachada compone la vista final para el cliente (por ejemplo `GameView`) consultando ambos endpoints y
unificando la información en una única respuesta para el frontend.

## Contratos de datos (visión rápida)

- `GameResponse` (Postgres): incluye `alternativeNames` y `coverUrl` entre sus campos.
- `GameDetailResponse` (Mongo): incluye `screenshots` y `videos`.
- El BFF compone ambos para devolver al cliente un único objeto agregado.

## Consideraciones operativas

- Mantener Postgres para datos transaccionales y con esquema y Mongo para contenido flexible reduce el tamaño de payload
  en consultas frecuentes.
- Evitar duplicación: cada dato tiene una única fuente de verdad (`Game` o `GameDetail`).
- Eventos de dominio (RabbitMQ/Spring AMQP) permiten notificar a servicios consumidores tras cambios en `Game` o
  `GameDetail`.

## Tests y calidad

El proyecto incluye pruebas unitarias e integración (Testcontainers para Postgres y Mongo en tests de integración). Las
pruebas cubren comportamiento del dominio (`Game`, `GameDetail`) y adaptadores de persistencia.

## Buenas prácticas

- El BFF debe consumir los endpoints internos y componer la vista agregada; el frontend no debe necesitar conocer la
  división interna.
- Mantener los VOs y las fábricas de dominio en la capa `domain`.
- No exponer entidades de persistencia fuera de la capa de infraestructura.

---

Para más detalles sobre la implementación, revisa los paquetes:

- `com.gamelisto.catalogo.domain` (entidades y VOs)
- `com.gamelisto.catalogo.application` (use cases y DTOs)
- `com.gamelisto.catalogo.infrastructure` (adaptadores: REST, persistencia, mappers)
