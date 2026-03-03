# busquedas (OpenSearch + sugerencias de videojuegos)

## 1) Objetivo

Este microservicio proporciona **sugerencias de videojuegos mientras el usuario escribe** en el buscador principal de la
web (autocomplete).

- El buscador es **el único medio de búsqueda** por ahora.
- No hay filtros, ni facetas, ni “búsqueda avanzada”.
- La sugerencia se basa en **título y nombres alternativos** (si existen).
- Al seleccionar una sugerencia, el frontend navega a la **vista del videojuego** (la vista la sirve el sistema
  principal / catálogo, no este servicio).

## 2) Idea general (explicación simple)

- **OpenSearch** se ejecuta como servicio externo (en Docker).
- `busquedas` mantiene en OpenSearch una **copia mínima** de videojuegos:
    - `gameId` (LONG, el id de IGDB)
    - `title` (título principal)
    - `alternativeNames` (lista de nombres alternativos; opcional)
- El catálogo es el **origen real** del dato. OpenSearch solo es una copia optimizada para sugerir rápido.

## 3) Responsabilidades y NO-responsabilidades

### ✅ Responsabilidades

1. Escuchar eventos que publica `catalogo` por RabbitMQ cuando:
    - se crea un videojuego
    - (opcional) se actualiza un videojuego
    - (opcional) se elimina un videojuego
2. Mantener OpenSearch sincronizado con esos eventos:
    - guardar / actualizar (upsert)
    - eliminar (si existe el evento)
3. Exponer un endpoint REST para autocomplete:
    - dado un texto parcial, devolver una lista corta de juegos (id + título)
    - el texto parcial puede coincidir con el título o con cualquier nombre alternativo

### ❌ NO-responsabilidades (fuera de alcance)

- No muestra detalle del juego.
- No filtra por género, plataforma, etc.
- No gestiona usuarios.
- No es la fuente de verdad de los videojuegos.

---

## 4) Flujo funcional

### 4.1 Indexación (desde catálogo hacia OpenSearch)

1. `catalogo` crea/actualiza/elimina un videojuego en su BD.
2. `catalogo` publica un evento.
3. `busquedas` consume el evento.
4. `busquedas` guarda en OpenSearch el documento mínimo:
    - `gameId`
    - `title`
    - `alternativeNames` (si viene)
5. Para las sugerencias, `busquedas` guarda un campo interno `nameSuggest` que incluye:
    - el `title`
    - y todos los `alternativeNames`

> Resultado: el usuario puede escribir “Elden Ring” o un nombre alternativo/localizado y el autocompletado seguirá
> funcionando.

### 4.2 Autocomplete (frontend → busquedas → OpenSearch)

1. El usuario escribe en el buscador.
2. El frontend llama a `GET /v1/games/suggest?q=...`
3. `busquedas` consulta OpenSearch y devuelve sugerencias.
4. El usuario selecciona un juego.
5. El frontend navega a `/games/{gameId}` (o la ruta equivalente del frontend).

---

## 5) Datos guardados en OpenSearch

### Documento “GameSearchDoc”

- `gameId` (LONG) → **ID IGDB**
- `title` (String)
- `alternativeNames` (List<String>) → opcional
- `nameSuggest` (campo interno para autocomplete)

**Regla:** el id del documento en OpenSearch debe ser `gameId` (así el “guardar/actualizar” es trivial y seguro).

Ejemplo (documento completo):

```json
{
  "gameId": 12345,
  "title": "Elden Ring",
  "alternativeNames": [
    "ELDEN RING",
    "Elden Ring™",
    "エルデンリング"
  ],
  "nameSuggest": {
    "input": [
      "Elden Ring",
      "ELDEN RING",
      "Elden Ring™",
      "エルデンリング"
    ]
  }
}
```

> Importante: para el tipo `completion`, OpenSearch espera que el campo contenga un objeto con `input` (lista de
> strings). No se recomienda guardar solo un array sin `input`.

---

## 6) OpenSearch: índice y estructura

### 6.1 Nombres recomendados (versionado)

- Índice físico: `games-v1`
- Alias de lectura: `games-read` → `games-v1`
- Alias de escritura: `games-write` → `games-v1`

### 6.2 Mapeo (simple)

Necesitamos:

- campos simples (`gameId`, `title`, `alternativeNames`)
- un campo `nameSuggest` para autocompletado por prefijo

Ejemplo (orientativo) de creación del índice:

```json
PUT games-v1
{
  "mappings": {
    "properties": {
      "gameId": {
        "type": "long"
      },
      "title": {
        "type": "text"
      },
      "alternativeNames": {
        "type": "text"
      },
      "nameSuggest": {
        "type": "completion"
      }
    }
  }
}
```

### 6.3 Reglas para rellenar `nameSuggest`

- Crear una lista `inputs = [title] + alternativeNames`
- Limpiar: `trim`, eliminar strings vacíos, eliminar duplicados (idealmente sin distinguir mayúsculas/minúsculas)
- Indexar como:

```json
"nameSuggest": {"input": ["title", "alt1", "alt2"]}
```

---

## 7) RabbitMQ: consumo de eventos de catálogo

### 7.1 Contrato mínimo de evento

**Evento:** `VideojuegoCreado` (y opcionalmente `VideojuegoActualizado` / `VideojuegoEliminado`)

Campos necesarios para indexar:

- `eventId` (string) → id único del evento (trazabilidad)
- `gameId` (long)
- `title` (string)
- `alternativeNames` (lista de strings; opcional)

Ejemplo:

```json
{
  "eventId": "2f7f2b2d-2c4e-4e4b-9d0a-9c5c1f3a5d21",
  "gameId": 12345,
  "title": "Elden Ring",
  "alternativeNames": [
    "Elden Ring™",
    "エルデンリング"
  ]
}
```

### 7.2 Nombres recomendados

- Exchange (topic): `catalog.events`
- Routing key:
    - `catalog.game.created`
    - (opcional) `catalog.game.updated`
    - (opcional) `catalog.game.deleted`
- Cola del microservicio:
    - `busqueda.catalog.games`
- (opcional) Cola de errores (DLQ):
    - `busqueda.catalog.games.dlq`

### 7.3 Qué hace el consumidor

- **created / updated:** upsert en OpenSearch con id = `gameId` y `nameSuggest.input` = `[title] + alternativeNames`
- **deleted:** borrar documento por id = `gameId` (si existe el evento)

---

## 8) API REST del microservicio

### 8.1 Endpoint de sugerencias

`GET /v1/games/suggest?q={texto}&size={n}`

- `q`: texto que escribe el usuario
- `size`: número máximo de sugerencias (por defecto `busquedas.suggest.default-size`, actualmente 4)

**Reglas:**

- Si `q` es vacío o tiene menos de `busquedas.suggest.min-chars` → devolver `[]`.
- Respuesta mínima: `gameId` y `title`.
- Aunque la coincidencia sea por un nombre alternativo, se devuelve siempre el **título principal**.

Ejemplo de respuesta:

```json
{
  "query": "eld",
  "results": [
    {
      "gameId": 12345,
      "title": "Elden Ring"
    },
    {
      "gameId": 99999,
      "title": "Eldest Souls"
    }
  ]
}
```

---

## 9) Lógica de consulta a OpenSearch (idea)

Ejemplo orientativo usando “completion suggest” (sobre `nameSuggest`), devolviendo solo `gameId` y `title` en `_source`:

```json
POST games-read/_search
{
  "_source": [
    "gameId",
    "title"
  ],
  "size": 0,
  "suggest": {
    "game-names": {
      "prefix": "eld",
      "completion": {
        "field": "nameSuggest",
        "size": 4,
        "skip_duplicates": true
      }
    }
  }
}
```

> Nota: por defecto OpenSearch puede devolver el documento completo en la sugerencia. Limitar `_source` evita respuestas
> grandes.

---

## 10) Estructura recomendada del proyecto (Spring Boot)

```text
busquedas/
  src/main/java/com/gamelisto/busquedas
    api/
      SuggestController.java
      dto/
        SuggestResponse.java
        SuggestItem.java
    application/
      mappers/
        GameSearchMapper.java
      usecases/
        IndexGameFromEventUseCase.java
        SuggestGamesUseCase.java
    domain/
      GameSearchDoc.java
      repositories/
        GameSearchRepositorio.java
    infrastructure/
      messaging/
        RabbitConfig.java
        CatalogEventsListener.java
        dto/
          VideojuegoCreadoEventDto.java
      opensearch/
        OpenSearchConfig.java
        GameSearchRepositorioOpenSearch.java
```

---

## 11) Configuración (properties)

```properties
spring.application.name=busquedas
server.port=8085
# OpenSearch
opensearch.url=http://localhost:9200
opensearch.index.read=games-read
opensearch.index.write=games-write
# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
busquedas.rabbit.exchange=catalog.events
busquedas.rabbit.queue=busqueda.catalog.games
busquedas.rabbit.routing.created=catalog.game.created
# Suggest
busquedas.suggest.min-chars=2
busquedas.suggest.default-size=4
```

---

## 12) Contrato de evento (DTO) — Java

DTO mínimo para deserializar el evento de catálogo:

```java
package com.gamelisto.busquedas.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VideojuegoCreadoEventDto(
        String eventId,
        long gameId,
        String title,
        List<String> alternativeNames
) {
}
```

Reglas recomendadas en el listener:

- Si `alternativeNames` viene `null`, tratarlo como lista vacía.
- Construir `nameSuggest.input` como: `title` + `alternativeNames` (sin duplicados ni strings vacíos).

---

## 13) Futuras ampliaciones (no implementar ahora)

- Añadir más campos (géneros, plataformas, año, etc.)
- Añadir endpoint de búsqueda “normal” con paginación
- Añadir filtros y contadores

