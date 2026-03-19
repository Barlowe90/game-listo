# Microservicio Catálogo — GameListo

## Resumen

`catalogo` es el servicio canónico de videojuegos del monorepo GameListo. Gestiona la información estructurada
de juegos (datos de consulta) y el contenido enriquecido (multimedia). Está diseñado siguiendo principios de
Arquitectura Hexagonal y DDD: dominio puro, casos de uso y adaptadores de infraestructura claramente separados.

## Contexto técnico

- Lenguaje: Java 21
- Framework: Spring Boot
- Organización: `domain` / `application` / `infrastructure`
- Persistencia: PostgreSQL (datos estructurados) + MongoDB (contenido enriquecido)
- Integración externa: IGDB (ingestión programada) y mensajería (RabbitMQ) para publicar eventos de dominio

## Objetivos del servicio

- Ofrecer una fuente de verdad para metadatos de videojuegos (titulo, resumen, plataformas, relaciones).
- Almacenar contenido multimedia (screenshots, videos) de forma desacoplada.
- Publicar eventos de dominio para que otros servicios (search, bff, biblioteca) consuman cambios.
- Mantener contratos REST estables para lectura de datos y operaciones de sincronización.

## Contratos HTTP (endpoints principales)

Base path: `/v1/catalogo`

| Método | Ruta                             | Auth / Rol          | Request                     | Response                          | Descripción / Notas                                                                                          |
|--------|----------------------------------|---------------------|-----------------------------|-----------------------------------|--------------------------------------------------------------------------------------------------------------|
| GET    | `/v1/catalogo/games`             | Public              | query params `page`, `size` | `List<GameCardResponse>` (200 OK) | Listado paginado ligero para cards: cover, nombre, plataformas y modos.                                     |
| GET    | `/v1/catalogo/games/{id}`        | Public              | path `id` (Long)            | `GameResponse` (200 OK)           | Metadatos canónicos del juego (Postgres).                                                                    |
| GET    | `/v1/catalogo/games/{id}/detail` | Public              | path `id` (Long)            | `GameDetailResponse` (200 OK)     | Contenido enriquecido (MongoDB): screenshots, videos, descripción larga.                                     |
| GET    | `/v1/catalogo/platforms`         | Public              | —                           | `List<PlatformResponse>` (200 OK) | Listado de plataformas soportadas.                                                                           |
| POST   | `/v1/catalogo/sync/games`        | Admin (recomendado) | —                           | `SyncStatusResponse` (200 OK)     | Dispara sincronización (IGDB) de juegos; en código hay un comentario `@PreAuthorize('ADMIN')` deshabilitado. |
| POST   | `/v1/catalogo/sync/platforms`    | Admin (recomendado) | —                           | `SyncStatusResponse` (200 OK)     | Dispara sincronización de plataformas desde IGDB.                                                            |

Notas adicionales

- Aunque los endpoints de sincronización no requieren autenticación en el código actual, en entornos de producción
  deberían protegerse para administradores.
- DTOs: `GameCardResponse`, `GameResponse`, `GameDetailResponse`, `PlatformResponse`, `SyncStatusResponse` se encuentran en
  `infrastructure/in/api/dto`.

## Consejos rápidos de lectura

- `Game` (Postgres) → datos canónicos y ligeros, indexables.
- `GameDetail` (Mongo) → contenido rico (screenshots, videos) que cambia con menos frecuencia.
- Ingestión desde IGDB → normalización → persistencia en Postgres/Mongo → publicación de eventos.

## Modelo de dominio (visión compacta)

- Game (aggregate root)
    - id (UUID/Long según implementación), name, summary, coverUrl, alternativeNames, platforms, genres, releaseDate,
      relaciones (dlcs, expansions, similares), flags de estado, metadatos transaccionales.
- GameDetail
    - Referencia a `gameId`, screenshots (List<String>), videos (List<String>), descripción larga y campos grandes.
- Proyección/DTOs
    - `GameCardResponse` (listado ligero), `GameResponse` (detalle canónico) y `GameDetailResponse` (contenido multimedia).

## Persistencia y decisiones operativas

- PostgreSQL
    - Guarda la estructura canónica de `Game`: esquemas relacionales, colecciones elementales y relaciones.
    - Buen candidato para consultas transaccionales y joins.
- MongoDB
    - Almacena `GameDetail` cuando el payload es voluminoso o flexible.
    - Reduce el peso de las consultas a Postgres cuando se requieren solo metadatos.

## Flujo de ingestión (alto nivel)

1. Scheduler o proceso manual solicita datos a IGDB (DTOs desde cliente IGDB).
2. Se normalizan y validan los datos (VOs del dominio).
3. Se persiste/actualiza `Game` en Postgres y `GameDetail` en MongoDB según sea necesario.
4. Se publican eventos de dominio (p. ej. `GameCreated`, `GameUpdated`) en RabbitMQ para downstream consumers.
5. Los consumidores (search, busquedas, bff) actualizan índices o caches a partir de estos eventos.

## Contratos de eventos (resumen)

- Exchange/Topic: `catalog.events`
- Eventos relevantes:
    - `catalog.game.created`
    - `catalog.game.updated`
    - `catalog.game.deleted`
- Payload mínimo recomendado: `{ eventId, gameId, type, timestamp, payload: { ...game fields... } }`

## Serialización y compatibilidad

- Usar JSON con propiedades conocidas (evitar breaking changes). Ser tolerante a campos adicionales
  (`@JsonIgnoreProperties(ignoreUnknown = true)` en DTOs consumidores).
- Versionar el contrato de eventos si se cambia el esquema de payload de forma no compatible.

## Mappers y anti-corruption layer

- Mappers en `infrastructure` traducen entre JPA/Mongo entities y VOs/domain entities.
- Mantener un ACL limpio para proteger el dominio de formatos externos (IGDB) y de detalles de persistencia.

## Testing

- Unit tests: lógica de dominio y validaciones (sin Spring).
- Integration tests: `@SpringBootTest` con perfiles que usan Testcontainers o H2; pruebas de repositorios con
  contenedores para Postgres y Mongo cuando es necesario.
- Recomendar ejecutar: `.\mvnw.cmd test` desde la carpeta `catalogo` (PowerShell).

## Comandos útiles (PowerShell)

```powershell
# Ejecutar tests
.\mvnw.cmd test;

# Ejecutar la aplicación en local
.\mvnw.cmd spring-boot:run;
```

## Configuración y variables importantes

- `spring.datasource.*` — datos para Postgres
- `spring.data.mongodb.*` — conexión a MongoDB
- Propiedades de IGDB (client id, token) — gestionadas fuera del repo (env vars / secrets)
- RabbitMQ config: host, user, pass, exchange/queue names
- `catalog.sync.*` — parámetros de la ingesta (batch size, rate limits)

## Observabilidad y operaciones

- Exponer métricas (Micrometer + Prometheus): latencia de ingestión, tiempos de persistencia, errores por tipo.
- Logs estructurados con traceId y eventId para correlación.
- Health checks para Postgres y MongoDB; readiness/liveness endpoints para orquestadores.

## Decisiones de diseño y trade-offs

- Separación Postgres/Mongo: reduce tamaño de filas transaccionales, mejora latencias en consultas ligeras.
- Normalización temprana: validar y mapear DTOs de IGDB a VOs del dominio para evitar lógica de negocio en adaptadores.
- Event-driven: favor consistencia eventual entre catálogo e índices/caches downstream; documentar SLAs de
  sincronización.

## Buenas prácticas para contribuciones

- Mantener PRs pequeños y con tests que cubran el comportamiento nuevo.
- Documentar cualquier cambio en contratos REST o en el esquema de eventos.
- Actualizar mappers cuando cambien las fuentes externas; preferir migraciones que no rompan consumidores.

## Referencias y ubicación del código

- Código fuente: `catalogo/src/main/java/com/gamelisto/catalogo`
- Tests: `catalogo/src/test/java`
- Configs y properties: `catalogo/src/main/resources`

## Licencia

El módulo se rige por la licencia del repositorio raíz.
