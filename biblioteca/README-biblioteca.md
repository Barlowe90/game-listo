# Microservicio Biblioteca – GameListo

Microservicio responsable de la **gestión de listas personales y estados/puntuaciones de videojuegos** dentro del
ecosistema GameListo. Implementa la creación y gestión de listas de juegos (ListasGames), la representación ligera de
usuarios (`UsuarioRef`) y juegos (`GameRef`) y el registro del estado y puntuación de cada juego en la biblioteca del
usuario.

## Tabla de Contenidos

- [Descripción del proyecto](#descripción-del-proyecto)
- [Responsabilidades del microservicio](#responsabilidades-del-microservicio)
- [Arquitectura](#arquitectura)
- [Modelo de datos](#modelo-de-datos)
- [API / Endpoints](#api--endpoints)
- [Seguridad](#seguridad)
- [Testing](#testing)
- [Ejecución local](#ejecución-local)
- [Variables de entorno](#variables-de-entorno)
- [Convenciones de código](#convenciones-de-código)

## Descripción del proyecto

El servicio `biblioteca` permite a los usuarios organizar su colección de videojuegos mediante listas personalizadas y
mantener el estado de cada juego (por ejemplo: deseado, jugándolo, completado). Para mantener bajo acoplamiento con el
servicio
`catalogo-service`, el microservicio almacena referencias ligeras a usuarios y juegos (`UsuarioRef`, `GameRef`) en lugar
de dependencias directas.

Es el aggregate root para la información de listas y estados de videojuegos a nivel de usuario en el dominio de
GameListo.

## Responsabilidades del microservicio

- Gestión completa de ListasGames (crear, renombrar, eliminar, visibilidad, tipo)
- Añadir / quitar `GameRef` a listas
- Registrar y actualizar `GameEstado` por juego y usuario (estado, fecha, puntuación)
- Permitir puntuaciones con escala 0–10 en pasos de 0.25
- Exponer API REST para operaciones CRUD y consultas de biblioteca
- Proveer endpoints simples de consulta (listas públicas/privadas, biblioteca por usuario)

## Arquitectura

Se sigue Arquitectura Hexagonal + DDD:

- `/domain` → Entidades del dominio (ListaGame, GameEstado, UsuarioRef, GameRef) y Value Objects
- `/application` → Casos de uso (CreateList, AddGameToList, UpdateGameState, RateGame, RemoveGame)
- `/infrastructure` → Adaptadores (REST controllers, persistence, mappers)

Regla de dependencias: `infrastructure` → `application` → `domain` (el dominio no conoce Spring ni JPA).

### Patrones y decisiones

- KISS: usar tipos simples cuando sea suficiente (por ejemplo, `List<String>` para urls de portada)
- Repositorios como puertos en el dominio, implementaciones JPA en `infrastructure/persistence/postgres`
- Mappers para convertir entre entidades JPA y agregados del dominio
- Tests mínimos viables: enfocarse en casos críticos del dominio

## Modelo de datos

Resumen de las principales entidades (conceptual):

- UsuarioRef
    - `usuarioId` (UUID)
    - `username` (String)
    - `avatar` (String, opcional)

- ListaGame (Aggregate)
    - `id` (UUID)
    - `usuario` (UsuarioRef)
    - `nombre` (String)
    - `tipo` (ENUM: PERSONAL, SISTEMA)
    - `visibilidad` (ENUM: PUBLICA, PRIVADA)
    - `games` (colección de `GameRef`)
    - `createdAt`, `updatedAt`

- GameRef
    - `gameId` (UUID — identificador en `catalogo-service`)
    - `name` (String)
    - `coverUrl` (String)

- GameEstado (Entidad ligada a Lista o Usuario)
    - `id` (UUID)
    - `usuarioId` (UUID)
    - `gameRef` (GameRef)
    - `estado` (ENUM: DESEADO, PENDIENTE, JUGANDO, PLATINANDO, COMPLETADO, ABANDONADO)
    - `puntuacion` (Decimal, 0.00–10.00, increments 0.25)
    - `updatedAt` (Instant)

### Reglas de negocio relevantes

- Un `Usuario` puede tener múltiples `ListasGames`.
- Un `GameRef` puede pertenecer a varias listas del mismo usuario.
- La puntuación acepta valores en pasos de 0.25; la API valida.
- `GameEstado` mantiene el histórico mínimo necesario (último estado y puntuación). Historial completo no es
  requisito para el TFG (KISS).

## API / Endpoints

Base path: `/v1/biblioteca`

### Health

- No añadirlo, usar el de Actuator

### Listas

- POST `/lists` → Crear lista
    - Body: `CrearListaRequest` (usuarioId, nombre, tipo, visibilidad)
    - Response: `ListaResponse` (201)

- PATCH `/lists/{listId}` → Renombrar/editar metadata
    - Body: `EditarListaRequest`
    - Response: `ListaResponse` (200)

- DELETE `/lists/{listId}` → Eliminar lista (soft delete si aplica)
    - Response: 204 No Content

- GET `/lists/{usuarioId}` → Listar listas de un usuario
    - Query: `publicOnly` (opcional)

### Gestión de juegos en listas

- POST `/lists/{listId}/games/{gameId}` → Añadir `GameRef` a lista
    - Body: `AddGameRequest` (gameId, name, coverUrl)
    - Response: 200 OK

- DELETE `/lists/{listId}/games/{gameId}` → Quitar `GameRef` de lista
    - Response: 200 OK

### Estado y puntuación de juegos

- POST `/users/{usuarioId}/games/{gameId}/state` → Crear/actualizar `GameEstado`
    - Body: `UpdateStateRequest` (estado, puntuacion?)
    - Response: `GameEstadoResponse` (200)

- POST `/users/{usuarioId}/games/{gameId}/rate` → Puntuar juego
    - Body: `RateRequest` (puntuacion decimal)
    - Response: 200 OK

## Seguridad

- Comprobración de roles de usuario para los endpoints.
- No irá a producción por lo que la seguridad será mínima.

## Testing

- Enfoque: tests muy básicos y representativos (KISS). Priorizar:
    - Validación de Value Objects
    - Comportamientos de `ListaGame` (crear, añadir/quitar juegos)
    - `GameEstado` (registro y actualización de estado/puntuación)

Comandos (desde la carpeta `biblioteca`):

```powershell
.\mvnw.cmd test; # Ejecuta tests
```

> Nota: Los tests de integración usarán Postgre; revisar `application-test.properties`.

## Ejecución local

Desde la carpeta `biblioteca`:

```powershell
.\mvnw.cmd spring-boot:run; # Ejecuta la aplicación.
```

O con Maven Wrapper en Unix-like:

```bash
./mvnw spring-boot:run
```

## Variables de entorno

Principales propiedades (ejemplos en `src/main/resources/application-local.properties`):

- `spring.datasource.url` — URL de la base de datos
- `spring.jpa.hibernate.ddl-auto` — `update` en dev
- Propiedades de conexión a `catalogo-service` (si se consumen) — URL y timeouts

## Convenciones de código

Sigue las mismas convenciones que el resto del mono-repo (`usuarios-service`):

- Paquetes: `domain`, `application`, `infrastructure`
- Puertos (interfaces) en `domain` o `application/ports` y adaptadores en `infrastructure`
- Mappers para convertir entre entidades JPA y agregados del dominio
- Value Objects inmutables con validación en fábrica `of()`
- Tests: AAA pattern y nombres en español `debe[Comportamiento]`

## Integración y buenas prácticas

- Antes de añadir carpetas o convensiones, revisar `usuarios-service` para mantener consistencia (por ejemplo, ubicación
  de `mapper` dentro de `persistence/postgres/mapper` si así está en usuarios-service).
- Mantener KISS: no añadir complejidad innecesaria para el TFG.

---

Referencias:

- Documento TFG (modelo de dominio y diagramas)
- `usuarios-service/README-usuarios.md` (estilo y convenciones)
