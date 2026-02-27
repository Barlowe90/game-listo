# README — Microservicio Catálogo

Este documento describe de forma concisa el microservicio **Catalogo** de GameListo: su propósito, arquitectura,
decisiones principales y cómo trabajar con él en desarrollo.

---

## Contexto rápido

- Proyecto: Trabajo de Fin de Grado (TFG). No está pensado para producción: prioridad KISS (Keep It Simple, Stupid).
- Objetivo: servir como *source of truth* para metadatos de videojuegos y gestionar la ingesta desde IGDB.
- Tecnologías principales: Java 21, Spring Boot 3.5.x, JPA (PostgreSQL) y MongoDB.

Principios importantes:

- Funcionalidad básica y código legible > sobre-ingeniería.
- Arquitectura Hexagonal + DDD (dominio, aplicación, infraestructura).
- No exponer entidades JPA fuera de la capa de infraestructura.

---

## Resumen funcional

El microservicio Catalogo:

- Mantiene el catálogo de juegos (agregado relacional `Game`).
- Almacena contenido enriquecido (capturas, videos, descripciones largas) en MongoDB como `GameDetail`.
- Ejecuta ingestas periódicas desde IGDB (scheduler) y realiza upserts en la base de datos relacional.
- Publica eventos (por ejemplo `GameCreated` / `GameUpdated`) para que otros servicios (search-service,
  biblioteca-service, publicaciones) se reindexen o actualicen.

---

## Estructura de capas (convención)

- domain/ — Lógica de dominio limpia (VOs, entidades, excepciones, puertos de repositorio)
- application/ — Casos de uso y DTOs (use cases que coordinan el dominio)
- infrastructure/ — Adaptadores: REST controllers, persistencia, mappers, integración con IGDB, mensajería

Regla de dependencia: infrastructure -> application -> domain

---

## Modelo mínimo

- Agregado relacional: `Game` (tabla `game`) — información canónica: id, nombre, fecha lanzamiento canónica,
  plataformas, etc.
- Documento no relacional: `GameDetail` (colección `game_detail` en MongoDB) — screenshots, videos, cover grande,
  nombres alternativos, contenido voluminoso.

Regla: `Game` es el núcleo; `GameDetail` contiene el contenido enriquecido y pesado.

---

## Endpoints expuestos (v1)

Implementados / esperados (JSON):

- POST /v1/catalogo/sync/games
    - Dispara una sincronización por ids o rango (body con lista de ids o criterios mínimos).
    - Respuesta: 202 Accepted + body con resumen del trabajo (ids procesados, estado).

Nota: la API debe usar DTOs en `infrastructure/api/dto` y no exponer entidades JPA.

---

## Ingesta desde IGDB

Objetivos y estrategia mínima viable (MVP):

- Ingesta inicial: paginación por id (ej. where id > lastId order by id asc limit 500).
- Job programado con `@Scheduled` para ingestiones incrementales.
- Persistencia: upsert en la tabla `game` (mapeo a entidad JPA y uso de `RepositorioGame` como puerto).
- Throttling y backoff: detectar 429 y aplicar reintentos con backoff exponencial sencillo.
- Normalización: convertir fechas a una `fecha de lanzamiento canónica` en UTC.

Recomendación práctica: mantener el código de cliente IGDB simple y desacoplado (una clase/adapter con métodos para
fetch/paginar).

---

## Eventos publicados

Después de crear/actualizar un juego, publicar un evento reducido con la información necesaria para:

- reindexación en search-service (OpenSearch)
- notificaciones a biblioteca-service
- actualizaciones en publicaciones

Formato mínimo del evento: { gameId, name, slug, releaseDate, platforms } (JSON). El envío se hace mediante RabbitMQ o
el adaptador de mensajería configurado.

---

## Persistencia y configuración

- PostgreSQL: información canónica de `Game`.
- MongoDB: colección `game_detail` con documento ligado por `gameId`.

Propiedades de configuración principales (application.properties / profiles):

- Datasource JDBC (Postgres): url, username, password, hibernate.ddl-auto (en dev puede ser `update`).
- MongoDB: uri y nombre de base.
- IGDB: CLIENT_ID y ACCESS_TOKEN (se mantienen en `.env` o en las variables de entorno locales).

---

## Desarrollo local — Quick start (Windows PowerShell)

1. Desde el directorio `catalogo` compilar y ejecutar con Maven (usa los wrappers incluidos):

```powershell
# Compilar
mvnw.cmd clean install

# Ejecutar (usa H2 o configuración local según profiles)
mvnw.cmd spring-boot:run
```

2. Si prefieres ejecutar con Docker Compose (recomendado para pruebas integradas con Postgres/Mongo):

```powershell
# Desde la raíz del repo (game-listo)
docker-compose up -d
```

3. Variables útiles (entorno/local):

- IGDB_CLIENT_ID
- IGDB_ACCESS_TOKEN
- SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD
- SPRING_DATA_MONGODB_URI

---

## Testing

Directrices MVP:

- Tests de dominio puros (sin Spring) para VOs y reglas de negocio.
- Tests de aplicación con Mockito para casos de uso.
- Tests de integración con Testcontainers si se quiere un entorno más real (Postgres + MongoDB). No es obligatorio para
  la entrega si el tiempo es limitado.

Comandos de ejemplo (Windows PowerShell):

```powershell
mvnw.cmd test
```

---

## Convenciones y buenas prácticas (breve)

- Mantener KISS: simplicidad y claridad en el código.
- Evitar value objects innecesarios: solo crear VOs cuando aporten valor real.
- No exponer entidades JPA fuera de `infrastructure`.
- Cada caso de uso (use case) es una clase `@Service` en `application/usecases`.
- DTOs entre capas: Request → Command → UseCase → DTO → Response.

---

## Próximos pasos (sugeridos)

- Implementar paginación robusta para la ingesta (checkpoint por lastId).
- Añadir reintentos con backoff configurable frente a 429/5xx de IGDB.
- Publicar eventos mínimos y pruebas de integración con el consumidor (search-service).
- Crear un pequeño endpoint de health para el job de sincronización.

---

## Referencias

- Documentación interna del proyecto y convenciones: revisar la carpeta raíz y el README general.
- IGDB API: almacenar credenciales manualmente y rotarlas cuando caduquen.

---

Archivo actualizado colaborativamente para el TFG — mantener simple y explicable en la defensa.
