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
servicio `catalogo-service`, el microservicio almacena referencias ligeras a usuarios y juegos (`UsuarioRef`, `GameRef`)
en lugar de dependencias directas.

Es el aggregate root para la información de listas y estados de videojuegos a nivel de usuario en el dominio de
GameListo.

## Responsabilidades del microservicio

- Gestión completa de ListasGames (crear, renombrar, eliminar, tipo)
- Añadir / quitar `GameRef` a listas
- Registrar y actualizar `GameEstado` por juego y usuario (estado, puntuación)
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

### Convenciones de IDs (IMPORTANTE para el agente)

- `userId` (API / path `/user/{userId}`) = `UsuarioRef.id` (UUID) **mismo ID que en `usuarios-service`**.
- `gameId` (API / path `/games/{gameId}`) = `GameRef.id` (Long) **mismo ID que en `catalogo-service`**.
- `listId` (API / path `/lists/{listId}`) = `ListaGame.id` (UUID) **interno del microservicio biblioteca**.

### Entidades principales (conceptual)

- UsuarioRef
    - `id` (UUID — mismo identificador que en `usuarios-service`)
    - `username` (String)
    - `avatar` (String, opcional)
    - Relaciones:
        - 1 UsuarioRef → N ListaGame
        - 1 UsuarioRef → N GameEstado (uno por cada juego que el usuario haya tocado)

- ListaGame (Aggregate Root)
    - `id` (UUID)
    - `usuarioRefId` (UUID)
    - `nombre` (String)
    - `tipo` (ENUM: PERSONALIZADA, OFICIAL)
    - Relaciones:
        - N ListaGame (PERSONALIZADA) ↔ N GameRef mediante ListaGameItem
        - Para listas OFICIALES: sus juegos **no** se almacenan en ListaGameItem, se obtienen desde `GameEstado` por
          `estado`.

- GameRef
    - `id` (Long — mismo identificador que en `catalogo-service`)
    - `name` (String)
    - `coverUrl` (String)

- GameEstado (Estado/puntuación por usuario y juego)
    - `id` (UUID)
    - `usuarioRefId` (UUID)
    - `gameRefId` (Long)
    - `estado` (ENUM: DESEADO, PENDIENTE, JUGANDO, PLATINANDO, COMPLETADO, ABANDONADO)
    - `puntuacion` (Decimal, 0.00–5.00, increments 0.25)
    - Nota: `GameEstado` **NO** referencia una lista. Es la “fuente de verdad” del estado del juego para el usuario.

- ListaGameItem (relación “juego en lista” para listas PERSONALIZADAS)
    - `listaId` (UUID)
    - `gameRefId` (Long)
    - PK compuesta: (`listaId`, `gameRefId`)
    - Uso: solo para listas de tipo PERSONALIZADA.

### Reglas de negocio relevantes

- Al crearse un UsuarioRef (evento desde `usuarios-service`) se deben inicializar las listas OFICIALES del usuario:
    - nombres: DESEADO, PENDIENTE, JUGANDO, PLATINANDO, COMPLETADO, ABANDONADO
    - `tipo = OFICIAL`
- `GameEstado` es único por usuario y juego:
    - Restricción recomendada: UNIQUE (`usuario_ref_id`, `game_ref_id`)
- `ListaGameItem` permite que un juego pertenezca a múltiples listas PERSONALIZADAS del mismo usuario:
    - Restricción: UNIQUE (`lista_id`, `game_ref_id`)
- Un `GameRef` puede aparecer simultáneamente:
    - en la lista OFICIAL derivada de su `GameEstado.estado` (p.ej. COMPLETADO)
    - y en una o más listas PERSONALIZADAS (vía ListaGameItem)
- Al eliminar una lista PERSONALIZADA:
    - se eliminan sus `ListaGameItem`
    - **no** se elimina `GameEstado` del juego (sigue existiendo y por tanto sigue apareciendo en la lista OFICIAL
      correspondiente)
- La puntuación acepta valores en pasos de 0.25 de 0 a 5; la API valida.
- `GameEstado` no mantiene histórico, solo el último estado y puntuación.

## API / Endpoints

Base path: `/v1/biblioteca`

### Health

- Usar el de Actuator

### Listas

- POST `/lists` → Crear lista
    - Body: `CrearListaRequest` (usuarioId, nombre, tipo, visibilidad)
    - Response: `ListaResponse` (201)

- PATCH `/user/{userId}/lists/{listId}` → Renombrar/editar metadata
    - Body: `EditarListaRequest` (nombreNuevo)
    - Response: `ListaResponse` (200)

- DELETE `/user/{userId}/lists/{listaId}` → Eliminar lista
    - Response: 204 No Content

- GET `/user/{userId}/lists/{listaId}` → Listar listas de un usuario
    - Response: `ListaResponse` (200)

- GET `/user/{userId}/lists/` → Listar todas las listas de un usuario
    - Response: `ListaResponse` (200)

### Gestión de juegos en listas

- POST `/user/{userId}/lists/{listaId}/games/{gameId}` → Añadir `GameRef` a lista
    - Response: 200 OK No Content

- DELETE `/user/{userId}/lists/{listaId}/games/{gameId}` → Quitar `GameRef` de lista
    - Response: 200 OK No Content

### Estado y puntuación de juegos

- POST `/user/{userId}/games/{gameId}/state` → Crear/actualizar `GameEstado`
    - Body: `UpdateStateRequest` (estado)
    - Response: `GameEstadoResponse` (200)

- POST `/user/{userId}/games/{gameId}/rate` → Puntuar `GameEstado`
    - Body: `RateRequest` (puntuacion decimal)
    - Response: `GameEstadoResponse` (200)

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
