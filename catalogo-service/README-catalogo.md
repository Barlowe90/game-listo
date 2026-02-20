# README — Microservicio Catálogo

Este documento sirve como **contexto de proyecto** con la arquitectura, el dominio y el stack del microservicio *
*Catálogo** de *GameListo*.

---

## ⚠️ Contexto: Proyecto TFG

**Este es un Trabajo de Fin de Grado (TFG) que NO saldrá a producción.**

### Principios de desarrollo:

#### 🎯 KISS (Keep It Simple, Stupid)

- ✅ **Funcionalidad básica que funcione**
- ✅ **Código legible y fácil de explicar en la defensa**
- ✅ **Arquitectura correcta pero sin sobre-ingeniería**
- ❌ **NO optimizaciones prematuras**
- ❌ **NO features complejas innecesarias**

#### 🧪 Testing Mínimo Viable

- Solo tests de casos principales (happy path + error crítico)
- NO tests exhaustivos de edge cases
- Testing manual con Postman/curl es válido

#### 📦 Simplicidad en Modelos

- **NO crear Value Objects complejos** si no aportan valor real al TFG
- Ejemplo: `List<String> screenshotUrls` en lugar de `List<Screenshot>` con validaciones

#### 📐 Arquitectura Pragmática

- **SÍ Hexagonal + DDD** (conceptos a demostrar)
- **SÍ PostgreSQL + MongoDB** (persistencia políglota)
- **NO abstracciones innecesarias** si algo se usa en un solo lugar

---

## 1) Objetivo del microservicio

El microservicio **Catálogo** es el *source of truth* de los **metadatos de videojuegos** dentro del sistema.  
Obtiene datos de **IGDB** y los persiste en dos almacenamientos:

1. **BBDD relacional**: metadato troncal y catálogos.
2. **Almacenamiento documental (NoSQL)**: contenido minimizado (nombre, cover, capturas y vídeos).

Además, el microservicio:

- Ejecuta **ingesta automatizada** desde IGDB (Spring Scheduler).
- Publica **eventos de integración** para:
    - disparar **reindexación** en OpenSearch (**search-service**),

### Integración con Otros Servicios

**catalogo-service NO implementa búsqueda avanzada** - esta responsabilidad está delegada a **search-service** (
OpenSearch):

- **search-service**: Consume eventos de RabbitMQ (`CatalogGameUpserted`) e indexa en OpenSearch
- **graphql-bff**: Llama a search-service para búsquedas y a catalogo-service para detalles completos
- **biblioteca-service**: Escucha eventos para validar que gameId existe antes de añadir a biblioteca

**Flujo de Búsqueda:**

```
Frontend → API Gateway → GraphQL BFF → search-service (OpenSearch) + catalogo-service (detalles)
```

## 2) Stack y estilo de implementación

- **Java 21**
- **Spring Boot 3.5.8**
- **Arquitectura**: Hexagonal (Ports & Adapters) + **DDD**
- **API**: Controlador **REST**
- **Scheduler**: Spring Scheduler para sincronización IGDB
- **Persistencia**:
    - Relacional: JPA/Hibernate (PostgreSQL)
    - Documental: MongoDB (colección `game_detail`)
- **Mensajería**: eventos (RabbitMQ)

---

## 3) Modelo de dominio (DDD)

### Agregado central (relacional)

- **Game** *(Aggregate Root)*  
  los actuales en Game.java

### Contenido enriquecido (documental)

- **GameDetail** (documento asociado a `game.id`)
    - `gameId` (igual a `Game.id`)
    - `screenshots[]` (subdocumentos)
    - `videos[]` (subdocumentos)
    - cover
    - alternative name

> Regla: `Game` es el “núcleo” (relacional) y `GameDetail` es “enriquecido/voluminoso” (documental).

---

## 4) Esquema de persistencia (mínimo actual)

### Relacional (PostgreSQL)

- **game**
  los que hay en Game.java

### Documental (MongoDB)

- **game_detail**
    - `_id` (ObjectId)
    - `gameId` (único, indexado)
    - `screenshots`: [{ `url`]
    - `videos`: [{ `url` ]
    - cover
    - alternative name

---

## 5) Endpoints REST (v1)

> Nombres tal como están definidos; implementar respuestas JSON simples con DTOs.

### Sincronización IGDB

- `POST /v1/catalogo/sync/games`
    - Objetivo: disparar sincronización por ids
    - Debe ser **idempotente** (upsert por `Game.id`)

### Consulta de detalle

> Consulta específica si es necesario actualizar un videojuego en concreto

- `GET /v1/catalogo/games/{idGame}/detail`
    - Devuelve: `Game` + `GameDetail` (si existe) + `GameTimeToBeat` (si existe)
    - Si falta `GameDetail`, devolver parcial sin error (o 404 según decisión)

> Para cuando un usuario busque un videojuego en el cuadro de búsqueda:
> - `GET /v1/catalogo/games?name=...` (o `q=...`) paginado

---

## 6) Ingesta IGDB

### Requisitos

- Ingesta inicial: **recorrer todos los juegos** vía paginación:
    - preferiblemente por **checkpoint** o decisión del programador senior: `where id > lastId; sort id asc; limit 500;`
- Campos mínimos a traer:
    - `id, name, summary, cover`
    - Para cover:
        - pedir `cover.url` y guardar URL
- Persistencia:
    - Upsert en `game`
    - Catálogos se completarán más adelante; por ahora dejar stubs o vacíos.
- Scheduler:
    - Job `@Scheduled(...)` para ejecutar incremental
- Checkpoint:
    - Persistir `lastSyncedGameId` en una tabla/colección `sync_state`
- Rate limit:
    - implementar throttling/backoff al recibir 429.

### Port & Adapter

> En estos apartado es una idea, que decida el programador senior:

- `IgdbClientPort` (dominio o application) con operaciones:
    - `fetchGamesBatch(afterId, limit): List<IgdbGameDto>`
    - (opcional) `fetchGameDetail(gameId)` si se enriquece luego
- Adapter HTTP:
    - Usa WebClient o RestClient
    - Body IGDB es texto tipo query DSL.

---

## 7) Eventos publicados

Tras persistir/actualizar juegos:

- Publicar un evento por lote o por juego (decisión MVP):
    - `CatalogGameUpserted` (o Created/Updated si se distingue)
    - `CatalogSyncBatchCompleted`
    - `CatalogSyncCompleted`

Estos eventos deben permitir:

- **reindexación** en OpenSearch (microservicio dedicado),
- **invalidación de cachés**.

Incluir en payload:

- `eventId`, `occurredAt`
- `gameId` (o lista)
- `source = IGDB`
- `traceId` si se usa OTel

---

## 8) Convenciones de implementación

- Uso de Arquitectura Hexagonal y DDD.
- DTOs para API y para IGDB; **no exponer entidades**.
- Tests:
    - Realizar test con mockito
    - Integration tests con Testcontainers (PostgreSQL + Mongo + RabbitMQ)
    - No usar H2 para test.

---

## 9) Prompts listos para Copilot (copia/pega)

### Prompt 1 — Crear entidades de dominio mínimas

> Implementa el dominio DDD del microservicio Catálogo con entidades: Game (id, name, summary, cover), Platform,
> Language, MultiplayerMode y GameTimeToBeat. Usa arquitectura hexagonal. Incluye Value Objects donde tenga sentido y
> validación básica (no nulos, strings no vacíos).

### Prompt 2 — Caso de uso SyncIgdbUseCase (checkpoint por id)

> Implementa SyncIgdbUseCase siguiendo arquitectura hexagonal: leer lastSyncedGameId desde SyncStateRepositoryPort,
> pedir a IgdbClientPort un batch con where id > lastId; sort id asc; limit 500, hacer upsert en GameRepositoryPort,
> actualizar checkpoint al max id, y publicar evento CatalogSyncBatchCompleted en EventPublisherPort. Maneja 429 con
> exponential backoff y reintentos limitados.

### Prompt 3 — Adapter IGDB con WebClient

> Implementa IgdbHttpAdapter (implements IgdbClientPort) usando Spring WebClient. Envía POST a /v4/games con body
> text/plain generado por IgdbQueryBuilder. Soporta fetchGamesBatch(afterId, limit) devolviendo DTOs con id, name,
> summary
> y cover.url. Maneja errores 4xx/5xx y 429 con retry/backoff.

### Prompt 5 — Persistencia JPA + Mongo

> Implementa adapters de persistencia: GameJpaEntity + Spring Data JPA repository y GameDetailDocument + Spring Data
> Mongo repository. Implementa los ports GameRepositoryPort y GameDetailRepositoryPort con mappers. Incluye una
> tabla/entidad SyncState (key/value) para guardar lastSyncedGameId.

---

## 10) Notas MVP

- El dominio tiene **pocos campos inicialmente** (`id`, `name`, `summary`, `cover`).  
  El diseño debe permitir ampliar campos sin romper capas.
- `GameDetail` se guarda aparte en documento por ser voluminosa y no estructurada.
- Los eventos se publican para que otros microservicios gestionen OpenSearch y cachés.

---
