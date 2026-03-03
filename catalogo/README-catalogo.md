# Microservicio Catálogo

## Modelo de Dominio: Game vs GameDetail

### 1. Principio de diseño

El modelo del microservicio Catálogo separa la entidad **Game** del agregado **GameDetail** siguiendo un criterio de
responsabilidad, patrón de acceso y volumen de datos.

Actualmente la clase `Game` contiene campos voluminosos que deben eliminarse durante el proceso de refactorización para
cumplir correctamente esta separación.

Cada dato debe tener una única fuente de verdad dentro del sistema.

---

## 2. Estado actual de la clase `Game`

Actualmente `Game` incluye los siguientes campos:

- id
- name
- summary
- coverUrl
- platforms
- gameType
- gameStatus
- alternativeNames
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
- screenshots
- videos

⚠️ Problema detectado:  
Los campos `alternativeNames`, `screenshots` y `videos` son estructuras potencialmente voluminosas o variables y no
deberían formar parte del núcleo estructurado del dominio.

---

## 3. Modelo objetivo tras refactorización

### 3.1 Game (núcleo estructurado)

Tras el refactor, `Game` deberá contener únicamente información estructurada y frecuentemente consultada:

- id
- name
- summary
- coverUrl
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

### 3.2 GameDetail (contenido enriquecido)

`GameDetail` deberá contener exclusivamente:

- alternativeNames (array)
- screenshots (array)
- videos (array)

`GameDetail` se vincula a `Game` mediante el identificador del juego.

---

## 4. Regla fundamental: No duplicación

Ningún atributo debe existir simultáneamente en `Game` y `GameDetail`.

Durante el refactor:

- Eliminar `alternativeNames` de `Game`
- Eliminar `screenshots` de `Game`
- Eliminar `videos` de `Game`
- Garantizar que estos campos residan exclusivamente en `GameDetail`

---

## 5. Exposición hacia el cliente (BFF)

El cliente no conoce la separación interna entre `Game` y `GameDetail`.

El sistema expone una única vista compuesta:

GET /games/{id}

### Flujo:

1. El cliente realiza una única petición.
2. La capa BFF:

- obtiene `Game`
- obtiene `GameDetail`
- compone una vista agregada (`GameView`).

3. Devuelve un único payload unificado.

El front nunca realiza dos llamadas separadas.

---

## 6. Justificación arquitectónica

La separación responde a:

- Optimización de payload en listados.
- Reducción de carga en consultas frecuentes.
- Escalabilidad independiente del contenido multimedia.
- Evolución desacoplada del contenido enriquecido.
- Claridad de responsabilidades en el dominio.

---

## 7. Instrucciones estrictas para la IA de refactorización

1. Eliminar de la entidad `Game` los campos:

- alternativeNames
- screenshots
- videos

2. Crear o actualizar `GameDetail` para que contenga esos campos.

3. Asegurar que:

- No exista duplicación en entidades.
- No exista duplicación en DTOs.
- No exista duplicación en documentos.

4. Mantener un único endpoint compuesto expuesto por la fachada (BFF).
