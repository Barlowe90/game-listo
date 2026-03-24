# Documento explicativo con fases para la implementación de GraphQL

## Alcance MVP

No se aplicará GraphQL a todos los microservicios. Se utilizará únicamente en una capa BFF para
demostrar que se comprende el modelo básico de GraphQL y que se ha integrado de forma realista dentro de la arquitectura
actual de GameListo.

La idea es la siguiente:

### Frontend

El frontend consumirá `POST /graphql` para las operaciones cubiertas por el MVP.

### Gateway

El gateway enruta `/graphql` hacia el nuevo microservicio `graphql-bff`.

## Nuevo microservicio `graphql-bff`

Este servicio:

- expone el schema GraphQL,
- implementa los resolvers,
- llama por REST a los microservicios internos necesarios,
- reenvía los headers de autenticación propagados por el gateway.

### Resto de microservicios

Se mantienen como están actualmente, usando sus APIs REST existentes.

Con este enfoque se demuestra una integración real sin necesidad de migrar toda la plataforma a GraphQL.

## Caso de uso MVP para GameListo

El MVP se congelará en un alcance pequeño y coherente con el dominio actual.

Se trabajará sobre **Catálogo + Biblioteca**, pero sin forzar todavía una agregación compleja que el backend aún no
expone limpiamente.

### Operaciones del MVP

- `game(id)` → devuelve datos del catálogo por identificador
- `setGameStatus(gameId, status, rating?)` → actualiza el estado del juego del usuario y su puntuación

### Operación aplazada a una segunda iteración

- `myGame(gameId)` → queda fuera del MVP inicial

La razón es que hoy biblioteca trabaja sobre `gameRefId` y su lectura actual está más orientada a recuperar estados
asociados a un juego que a resolver de forma directa el estado del usuario autenticado para ese juego. Por tanto, antes
de añadir `myGame(gameId)` conviene exponer en biblioteca una lectura específica del tipo:

- estado del usuario autenticado para `gameId`

Solo después de eso tendría sentido incorporar esta query al schema GraphQL.

## Qué se quiere demostrar con este MVP

Con este alcance se puede demostrar lo esencial de GraphQL sin sobrecomplicar el proyecto:

- una query simple,
- una mutation,
- un schema tipado,
- resolvers anotados con Spring,
- composición de llamadas REST internas desde un BFF,
- adaptación de la respuesta al frontend para pedir solo los campos necesarios en tarjetas y listados.

## Qué dejaría fuera del MVP

Para mantener la implementación simple, se dejan fuera:

- subscriptions,
- WebSocket,
- federation,
- paginación compleja,
- DataLoader,
- GraphQL en todos los microservicios,
- schema grande de toda la plataforma,
- separación fino-granular de permisos por operación GraphQL.

La arquitectura quedaría así:

`Next.js frontend -> Gateway -> graphql-bff -> catálogo / biblioteca`

Puntos importantes:

- el gateway no resuelve GraphQL;
- el gateway solo enruta y aplica concerns transversales;
- el conocimiento del schema y la ejecución de resolvers vive en `graphql-bff`;
- los microservicios internos siguen exponiendo REST.

Esto permite explicar de forma clara en la memoria que GraphQL se usa como **BFF** y no como sustituto completo de las
APIs internas.

## Decisión de seguridad del MVP

Como la arquitectura actual decide el acceso a partir del **path** y no de la operación GraphQL concreta, mezclar en un
único `/graphql` operaciones públicas y privadas complica innecesariamente la seguridad.

Por ello, para este MVP se tomará una decisión simple:

- el endpoint `/graphql` será **privado**.

Esto permite incluir en el mismo endpoint tanto `game(id)` como `setGameStatus(...)` sin tener que resolver todavía
autorización distinta por operación.

Más adelante, si se quisiera una query pública de catálogo por GraphQL, habría que estudiar una de estas opciones:

- segundo endpoint GraphQL público,
- capa adicional de autorización por operación,
- mantener las consultas públicas por REST y reservar GraphQL para operaciones autenticadas.

## Propagación de autenticación

En la arquitectura actual, el gateway añade headers como:

- `X-User-Id`
- `X-User-Roles`

Los microservicios confían en esos headers para identificar al usuario autenticado. Por tanto, el BFF
GraphQL no puede ignorarlos.

El flujo correcto en este MVP será:

1. el cliente llama a `/graphql` a través del gateway,
2. el gateway valida el acceso y añade los headers de identidad,
3. `graphql-bff` recibe esos headers,
4. `graphql-bff` reenvía esos mismos headers cuando invoca a catálogo o biblioteca.

Sin esta propagación, la mutation `setGameStatus(...)` no podría ejecutarse correctamente en el contexto del usuario
autenticado.

## Plan de implementación por fases

## Fase 1. Crear el microservicio `graphql-bff`

Crear un servicio Spring Boot independiente con:

- `spring-boot-starter-graphql`
- un starter web (`spring-boot-starter-web` o `spring-boot-starter-webflux`)

Resultado esperado:

- el servicio arranca correctamente,
- expone `POST /graphql`,
- compila y puede ejecutarse de forma aislada.

## Fase 2. Integrar el módulo en el proyecto backend

Antes de seguir con el desarrollo funcional, hay que completar las piezas mecánicas del proyecto:

- incluir el módulo `graphql` en el agregador Maven de `backend`,
- comprobar que participa correctamente en el build global,
- dejar preparada su configuración base.

Resultado esperado:

- `mvn clean test` desde `backend` reconoce el nuevo módulo,
- el servicio forma parte de la estructura oficial del proyecto.

## Fase 3. Definir un schema mínimo en modo schema-first

Se definirá un schema mínimo en `src/main/resources/graphql/**`.

Para este MVP bastará con algo de este estilo:

```graphql
 type Query {
    game(id: ID!): Game
}

type Mutation {
    setGameStatus(input: SetGameStatusInput!): GameStatusPayload
}

type Game {
    id: ID!
    title: String!
    coverUrl: String
    platform: String
}

input SetGameStatusInput {
    gameId: ID!
    status: String!
    rating: Int
}

type GameStatusPayload {
    gameId: ID!
    status: String!
    rating: Int
}
```

Con esto se demuestra:

- `Query`,
- `Mutation`,
- tipos,
- inputs,
- contrato explícito entre frontend y BFF.

## Fase 4. Implementar la query `game(id)`

Se implementará la primera query real del MVP:

- `game(id)`

Esta query llamará al microservicio de catálogo usando el identificador real que hoy expone el dominio, evitando
introducir `slug` antes de tiempo.

Objetivo de esta fase:

- validar el extremo completo GraphQL → resolver → REST interno → respuesta tipada,
- permitir al frontend pedir solo los campos que necesita para fichas resumidas, tarjetas o listados.

## Fase 5. Implementar la mutation `setGameStatus(...)`

Se implementará la mutation principal del MVP:

- `setGameStatus(input)`

Esta operación es viable, pero hay que dejar claro desde el diseño que **biblioteca no la resuelve hoy con un único
endpoint REST**. En la implementación actual, cambiar el estado y puntuar son operaciones separadas.

Por tanto, el resolver GraphQL deberá actuar como orquestador:

- si llega solo `status`, realizará la llamada REST de cambio de estado,
- si llega también `rating`, realizará además la llamada REST correspondiente a puntuación,
- devolverá un payload unificado al frontend.

Esto encaja muy bien con el papel de GraphQL como BFF.

## Fase 6. Reenviar identidad y contexto de seguridad

Antes de conectar con biblioteca, el BFF debe propagar correctamente:

- `X-User-Id`
- `X-User-Roles`

Tarea concreta:

- capturar los headers entrantes en el request GraphQL,
- reenviarlos en las llamadas REST internas.

Resultado esperado:

- las llamadas a biblioteca siguen funcionando como si llegaran desde el gateway directamente,
- el BFF no rompe el modelo de seguridad actual.

## Fase 7. Añadir la ruta `/graphql` en el gateway

Una vez el BFF funcione en standalone, hay que conectarlo a la arquitectura principal:

- añadir la ruta `/graphql` en el gateway,
- enlazarla con `graphql-bff`,
- revisar la configuración necesaria en `application.properties`.

Resultado esperado:

- el frontend accede a GraphQL a través del gateway,
- no se consume el BFF de forma directa desde el cliente.

## Fase 8. Manejo básico de errores

El MVP debe contemplar al menos estos errores:

- juego no encontrado en catálogo,
- estado inválido,
- valoración inválida,
- error interno al orquestar una de las llamadas REST.

No hace falta una estrategia muy avanzada, pero sí una capa mínima de traducción de errores para no filtrar respuestas
REST internas directamente al frontend.

## Fase 9. Tests mínimos de GraphQL

Añadir una batería mínima de pruebas para demostrar que la integración está validada:

- la query `game(id)` devuelve datos correctos,
- la mutation `setGameStatus(...)` actualiza el estado,
- la mutation `setGameStatus(...)` orquesta también la puntuación cuando `rating` está presente,
- los headers de identidad se reenvían correctamente a los servicios internos.

## Fase 10. Segunda iteración opcional: `myGame(gameId)`

Solo después de que biblioteca exponga una lectura específica por usuario autenticado, se podrá añadir:

- `myGame(gameId)`

En ese momento sí tendría sentido que el BFF agregue:

- datos del juego desde catálogo,
- estado personal del usuario desde biblioteca.

Esa query quedaría como una mejora natural del MVP, no como requisito de la primera entrega.

## Orden real de trabajo

El orden recomendado sería este:

1. Crear `graphql-bff`
2. Añadir el módulo al agregador Maven
3. Exponer `POST /graphql`
4. Definir el schema mínimo
5. Implementar `game(id)`
6. Implementar `setGameStatus(input)`
7. Reenviar headers de identidad
8. Añadir la ruta `/graphql` en el gateway
9. Añadir manejo básico de errores
10. Añadir tests
11. Conectar el frontend a una query real
12. Dejar `myGame(gameId)` para una segunda iteración

## Resultado esperado del MVP

Se considerará completado el MVP cuando existan, al menos:

- un microservicio `graphql-bff`,
- un schema GraphQL mínimo,
- una query funcional `game(id)`,
- una mutation funcional `setGameStatus(...)`,
- propagación correcta de identidad desde gateway hacia los microservicios internos,
- ruta `/graphql` operativa en el gateway,
- tests básicos que validen el flujo principal.

Con esto se demuestra que se entienden los fundamentos de GraphQL y que se ha incorporado de forma realista en la
arquitectura de GameListo sin sobredimensionar el alcance del TFG.

## Estado de la implementación de las fases

[] Fase 1. Crear el microservicio `graphql-bff`
[] Fase 2. Integrar el módulo en el proyecto backend
[] Fase 3. Definir un schema mínimo en modo schema-first
[] Fase 4. Implementar la query `game(id)`
[] Fase 5. Implementar la mutation `setGameStatus(...)`
[] Fase 6. Reenviar identidad y contexto de seguridad
[] Fase 7. Añadir la ruta `/graphql` en el gateway
[] Fase 8. Manejo básico de errores
[] Fase 9. Tests mínimos de GraphQL
[] Fase 10. Segunda iteración opcional: `myGame(gameId)`

