# busquedas — servicio de sugerencias (OpenSearch)

Resumen

`busquedas` es un microservicio responsable del autocompletado/sugerencias de videojuegos. Mantiene en OpenSearch
una copia optimizada y mínima del catálogo (id, título y nombres alternativos) y expone un endpoint REST para
sugerencias rápidas. Está pensado para ser llamado por el frontend o por un BFF en el flujo de búsqueda.

Objetivos de diseño

- Latencia baja en autocompletado (operaciones de lectura optimizadas en OpenSearch).
- Contratos simples y estables para integración con `catalogo` (eventos) y consumidores (HTTP).
- Separación clara entre fuente de verdad (catalogo) y capa de búsqueda (indice en OpenSearch).

Contenido de este README

- Contratos HTTP
- Contrato de eventos desde `catalogo`
- Estructura del documento en OpenSearch y mapeos recomendados
- Configuración y ejecución local
- Consideraciones operativas y de diseño

Contratos HTTP

Base path: `/v1/busquedas`

| Método | Ruta                                          | Auth / Rol | Request                  | Response                       | Descripción / Notas                                                                            |
|--------|-----------------------------------------------|------------|--------------------------|--------------------------------|------------------------------------------------------------------------------------------------|
| GET    | `/v1/busquedas/sugerencia?q={texto}&size={n}` | Public     | query params `q`, `size` | `SugerenciasResponse` (200 OK) | Devuelve sugerencias (autocompletado). Parámetros: `q` requerido, `size` opcional (default 5). |

Endpoint de sugerencias

GET `/v1/busquedas/sugerencia?q={texto}&size={n}`

- Parámetros
    - `q` (required): texto del usuario. Si es vacío o inferior a `busquedas.suggest.min-chars` => devolver `[]`.
    - `size` (optional): número máximo de resultados. Valor por defecto en el controlador: `5`.

- Respuesta mínima (JSON):

  {
  "query": "eld",
  "results": [
  { "gameId": 12345, "title": "Elden Ring" },
  { "gameId": 99999, "title": "Eldest Souls" }
  ]
  }

- Reglas operativas
    - La coincidencia puede producirse por `title` o por cualquiera de los `alternativeNames`, pero la respuesta
      siempre devuelve el `title` principal y el `gameId`.
    - Validar y normalizar `q` (trim, longitud mínima, máximo razonable).

Evento: contrato con `catalogo`

Este servicio consume eventos publicados por `catalogo` para mantener el índice sincronizado.

Evento mínimo esperado (JSON):

{
"eventId": "uuid",
"gameId": 12345,
"title": "Elden Ring",
"alternativeNames": ["Elden Ring™", "エルデンリング"]
}

Recomendaciones sobre el flujo de consumo

- Exchange (topic): `catalog.events`
- Routing keys:
    - `catalog.game.created`
    - `catalog.game.updated`
    - `catalog.game.deleted`
- Cola del servicio: `busqueda.catalog.games`

Procesamiento del evento

- `created` / `updated`: realizar upsert en OpenSearch con id = `gameId`. Construir `nameSuggest.input` a partir de
  `[title] + alternativeNames` (limpiar, deduplicar, trim).
- `deleted`: eliminar documento por id cuando llegue evento de borrado.
- Guardar `eventId` en logs para trazabilidad.

Documento indexado en OpenSearch (GameSearchDoc)

Campos esenciales:

- `gameId` (long) — id del juego en catálogo (IGDB o equivalente).
- `title` (text) — título principal.
- `alternativeNames` (text[]) — lista opcional.
- `nameSuggest` (completion) — campo para autocompletado.

Ejemplo de documento (formato final):

{
"gameId": 12345,
"title": "Elden Ring",
"alternativeNames": ["Elden Ring™", "エルデンリング"],
"nameSuggest": { "input": ["Elden Ring", "Elden Ring™", "エルデンリング"] }
}

Mapeo recomendado (simplificado)

PUT /games-v1
{
"mappings": {
"properties": {
"gameId": { "type": "long" },
"title": { "type": "text", "fields": { "keyword": { "type": "keyword" } } },
"alternativeNames": { "type": "text" },
"nameSuggest": { "type": "completion" }
}
}
}

Índices y alias

- Índice físico: `games-v1`
- Aliases recomendados:
    - `games-read` → lectura
    - `games-write` → escritura

Operaciones contra el índice

- Upsert: indexar documento con `id = gameId` para simplificar sincronización.
- Delete: eliminar por id cuando llegue evento de borrado.

Consulta de sugerencias (ejemplo)

Usar la sugerencia de tipo completion sobre `nameSuggest` y limitar `_source` a `gameId` y `title`:

POST /{alias-read}/_search
{
"_source": ["gameId","title"],
"suggest": {
"game-names": {
"prefix": "eld",
"completion": { "field": "nameSuggest", "size": 4, "skip_duplicates": true }
}
}
}

Consideraciones de diseño y operativas

- Normalización: limpiar entradas (trim), eliminar duplicados y normalizar mayúsculas/minúsculas en la etapa de
  indexado.
- Rendimiento: campo `completion` en OpenSearch está optimizado para baja latencia en autocompletado; monitorizar uso de
  memoria y dimensiones del índice.
- Consistencia eventual: el servicio es event-driven; la búsqueda es eventualmente consistente respecto al catálogo.
- Trazabilidad: loggear `eventId` y `gameId` por cada procesamiento para facilitar debugging y reconciliación.

Configuración (properties)

Valores relevantes (ejemplo):

```properties
spring.application.name=busquedas
server.port=8085
opensearch.url=http://localhost:9200
opensearch.index.read=games-read
opensearch.index.write=games-write
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
busquedas.rabbit.exchange=catalog.events
busquedas.rabbit.queue=busqueda.catalog.games
busquedas.rabbit.routing.created=catalog.game.created
busquedas.suggest.min-chars=2
busquedas.suggest.default-size=4
```

DTOs y ejemplos (Java)

Evento DTO mínimo que el listener debe deserializar:

```java

@JsonIgnoreProperties(ignoreUnknown = true)
public record VideojuegoCreadoEventDto(
        String eventId,
        long gameId,
        String title,
        List<String> alternativeNames
) {
}
```

Ejemplo de respuesta del endpoint de sugerencias (JSON):

{
"query": "eld",
"results": [ { "gameId": 12345, "title": "Elden Ring" } ]
}

Ejecución local y pruebas

Desde la raíz del proyecto o desde `busquedas/`:

```powershell
# Tests
.\mvnw.cmd test;

# Ejecutar servicio
.\mvnw.cmd spring-boot:run;
```