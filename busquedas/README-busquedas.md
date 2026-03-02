# busquedas (OpenSearch + sugerencias de videojuegos)

## 1) Objetivo

Este microservicio proporciona **sugerencias de videojuegos mientras el usuario escribe** en el buscador principal de la web (autocomplete).

- El buscador es **el único medio de búsqueda** por ahora.
- No hay filtros, ni facetas, ni “búsqueda avanzada”.
- La sugerencia se basa en **título y nombres alternativos** (si existen).
- Al seleccionar una sugerencia, el frontend navega a la **vista del videojuego** (la vista la sirve el sistema principal / catálogo, no este servicio).

## 2) Idea general (explicación simple)

- **OpenSearch** se ejecuta como servicio externo (en Docker).
- `busquedas` mantiene en OpenSearch una **copia mínima** de videojuegos:
  - `gameId` (LONG, el id de IGDB)
  - `title` (título principal)
  - `alternativeNames` (lista de nombres alternativos; opcional)
- El catálogo es el **origen real** del dato. OpenSearch solo es una copia optimizada para sugerir rápido.

## 3) Responsabilidades y NO-responsabilidades

### ✅ Responsabilidades

1. Escuchar eventos que publica `catalogo-service` por RabbitMQ cuando:
   - se crea un videojuego
   - (opcional) se actualiza un videojuego
   - (opcional) se elimina un videojuego
2. Mantener OpenSearch sincronizado con esos eventos:
   - guardar / actualizar (upsert)
   - eliminar (si existe el evento)
3. Exponer un endpoint REST para autocomplete:
   - dado un texto parcial, devolver una lista corta de juegos (id + título)
   - **el texto parcial puede coincidir con el título o con cualquier nombre alternativo**

### ❌ NO-responsabilidades (fuera de alcance)

- No muestra detalle del juego.
- No filtra por género, plataforma, etc.
- No gestiona usuarios.
- No es la fuente de verdad de los videojuegos.

---

## 4) Flujo funcional

### 4.1 Indexación (desde catálogo hacia OpenSearch)

1. `catalogo-service` crea un videojuego en su BD.
2. `catalogo-service` publica evento `VideojuegoCreado` (y opcionalmente `VideojuegoActualizado` / `VideojuegoEliminado`).
3. `busquedas` consume el evento.
4. `busquedas` guarda en OpenSearch el documento mínimo:
   - `gameId`
   - `title`
   - `alternativeNames` (si viene)
5. Para las sugerencias, `busquedas` guarda un campo interno (por ejemplo `nameSuggest`) que incluye:
   - el `title`
   - y todos los `alternativeNames`

> Resultado: el usuario puede escribir **“Elden Ring”** o, por ejemplo, un nombre alternativo/localizado, y el autocompletado seguirá funcionando.

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

**Regla:** el id del documento en OpenSearch debe ser `gameId` (así el “guardar/actualizar” es trivial y seguro).

Ejemplo de documento:

```json
{
  "gameId": 12345,
  "title": "Elden Ring",
  "alternativeNames": ["ELDEN RING", "Elden Ring™", "エルデンリング"]
}
```

---

## 6) OpenSearch: índice y estructura

### 6.1 Nombres recomendados (versionado)

- Índice físico: `games-v1`
- Alias de lectura: `games-read` → `games-v1`
- Alias de escritura: `games-write` → `games-v1`

> Esto permite cambiar a `games-v2` en el futuro sin romper clientes (solo se actualizan aliases).

### 6.2 Mapeo (simple)

Necesitamos:

- campos simples (`gameId`, `title`, `alternativeNames`)
- un campo “especial” de sugerencias que contenga **título + nombres alternativos**

Ejemplo (orientativo) de creación del índice:

```json
PUT games-v1
{
  "mappings": {
    "properties": {
      "gameId": { "type": "long" },
      "title":  { "type": "text" },
      "alternativeNames": { "type": "text" },

      "nameSuggest": { "type": "completion" }
    }
  }
}
```

### 6.3 Cómo se rellena el campo de sugerencias

Al indexar, se rellena `nameSuggest` con una lista de entradas:

- primero el `title`
- luego cada elemento de `alternativeNames` (si existe)

Ejemplo:

```json
{
  "gameId": 12345,
  "title": "Elden Ring",
  "alternativeNames": ["Elden Ring™", "エルデンリング"],
  "nameSuggest": ["Elden Ring", "Elden Ring™", "エルデンリング"]
}
```

> Nota: si hay duplicados (por ejemplo el título repetido en alternativos), se recomienda eliminarlos antes de indexar.

---

## 7) RabbitMQ: consumo de eventos de catálogo

### 7.1 Contrato mínimo de evento

**Evento:** `VideojuegoCreado`  
Campos necesarios:

- `eventId` (string) → id único del evento (para trazabilidad)
- `gameId` (long)
- `title` (string)
- `alternativeNames` (lista de strings; opcional)

Ejemplo:

```json
{
  "eventId": "2f7f2b2d-2c4e-4e4b-9d0a-9c5c1f3a5d21",
  "gameId": 12345,
  "title": "Elden Ring",
  "alternativeNames": ["Elden Ring™", "エルデンリング"]
}
```

### 7.2 Nombres recomendados (para que quede claro en el proyecto)

> Si ya tenéis un estándar de exchanges/routing keys, se adapta a ese estándar.

- Exchange (topic): `catalog.events`
- Routing key:
  - `catalog.game.created`
  - (opcional) `catalog.game.updated`
  - (opcional) `catalog.game.deleted`
- Cola del microservicio búsqueda:
  - `busqueda.catalog.games`
- (opcional) Cola de errores (DLQ):
  - `busqueda.catalog.games.dlq`

### 7.3 Qué hace el consumidor

- **Al recibir `created` o `updated`:**
  - upsert en OpenSearch con id = `gameId`
  - rellena `nameSuggest` con `[title] + alternativeNames`
- **Al recibir `deleted` (si existe):**
  - borrar documento por id = `gameId`

**Nota de robustez (simple):**

- Si llega el mismo evento dos veces, no pasa nada: el upsert deja el documento igual.
- Si OpenSearch no está disponible, se reintenta.

---

## 8) API REST del microservicio

### 8.1 Endpoint de sugerencias

`GET /v1/games/suggest?q={texto}&size={n}`

- `q`: texto que escribe el usuario
- `size`: número máximo de sugerencias (por defecto 4)

**Reglas:**

- Si `q` es vacío o tiene menos de 2 caracteres → devolver `[]` (para no saturar).
- Respuesta mínima: `gameId` y `title`.
- Aunque la coincidencia sea por un nombre alternativo, se devuelve siempre el **título principal** (para que la UI sea consistente).

Ejemplo de respuesta:

```json
{
  "query": "eld",
  "results": [
    { "gameId": 12345, "title": "Elden Ring" },
    { "gameId": 99999, "title": "Eldest Souls" }
  ]
}
```

### 8.2 Errores esperables

- `400 Bad Request` si falta `q`
- `503 Service Unavailable` si OpenSearch no responde

---

## 9) Lógica de consulta a OpenSearch (idea)

La consulta debe devolver sugerencias rápidas por prefijo del texto.

Ejemplo orientativo usando “suggest” (sobre `nameSuggest`):

```json
POST games-read/_search
{
  "size": 0,
  "suggest": {
    "game-names": {
      "prefix": "eld",
      "completion": {
        "field": "nameSuggest",
        "size": 8
      }
    }
  }
}
```

El servicio transforma eso en `results: [{gameId,title}, ...]`.

---

## 10) Estructura recomendada del proyecto (Spring Boot)

Estructura simple (hexagonal/DDD-friendly, sin complicarlo):

```text
busquedas/
  src/main/java/.../busquedas
    infrastructure/
      opensearch/
        OpenSearchConfig.java
        GameSearchIndexRepository.java
      messaging/
        RabbitConfig.java
        CatalogEventsListener.java
    application/
      SuggestGamesHandle.java
      SuggestGamesUseCase.java
      IndexGameFromEventHandle.java
      IndexGameFromEventUseCase.java
    domain/
      GameSearchDoc.java
    api/
      SuggestController.java
      dto/
        SuggestResponse.java
```

**Responsabilidad de cada capa (simple):**

- `messaging`: escucha eventos y llama al caso de uso
- `application`: lógica “qué hacer”
- `opensearch`: guardar / consultar en OpenSearch
- `api`: endpoint REST

---

## 11) Configuración (variables / properties)

Ejemplo de properties (adaptar al estándar):

```properties
# OpenSearch
opensearch.url=http://opensearch:9200
opensearch.index.read=games-read
opensearch.index.write=games-write

# RabbitMQ
rabbitmq.host=rabbitmq
rabbitmq.exchange=catalog.events
rabbitmq.queue=busqueda.catalog.games
rabbitmq.routing.created=catalog.game.created
```

---

## 12) Docker local (mínimo)

Este servicio necesita (en local):

- RabbitMQ (ya existe en el proyecto)
- OpenSearch (y opcional OpenSearch Dashboards)

> La definición exacta del `docker-compose.yml` se deja al repositorio principal, pero el concepto es:

- `opensearch` expone 9200
- `busquedas` se conecta a `opensearch:9200`

---

## 13) Pruebas (lo mínimo que debe existir)

### Unitarias

- Dado un evento `VideojuegoCreado` → se construye el documento correcto (gameId + title + alternativeNames)
- Dado `q="el"` → se llama a OpenSearch y se mapea la respuesta a DTO

### Integración (recomendado)

- Levantar OpenSearch en entorno de test
- Indexar 3-5 juegos con algunos nombres alternativos
- Consultar `suggest` con texto que coincida por alternativo y verificar que devuelve el juego correcto

---

## 14) Decisiones actuales (para evitar ambigüedad)

- Se indexa: `gameId`, `title`, `alternativeNames`
- Solo hay un endpoint público de uso: `GET /v1/games/suggest`
- Solo se usa en el buscador superior de la web
- No hay filtros ni otras búsquedas

---

## 15) Futuras ampliaciones (no implementar ahora)

- Añadir más campos (géneros, plataformas, año, etc.)
- Añadir endpoint de búsqueda “normal” con paginación
- Añadir filtros y contadores
- Mejorar relevancia, sinónimos, etc.

---

## 16) Contrato de evento (DTO) — Java

Este es el DTO mínimo que `busquedas` necesita para deserializar el evento `VideojuegoCreado`.

```java
package com.gamelisto.busqueda.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VideojuegoCreadoEventDto(
    String eventId,
    long gameId,
    String title,
    List<String> alternativeNames
) {}
```

### Reglas simples recomendadas en el listener

- Si `alternativeNames` viene `null`, tratarlo como lista vacía.
- Construir `nameSuggest` como: `title` + `alternativeNames` (sin duplicados).

Ejemplo de construcción (idea):

```java
// pseudocódigo
var names = new ArrayList<String>();
names.add(event.title());
if (event.alternativeNames() != null) names.addAll(event.alternativeNames());
names = deduplicate(names);
index(names);
```
