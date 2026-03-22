# Documento explicativo con fases para la implementación de graphQL

## Alcance MVP

No lo pondría en todos los microservicios.

Haría esto:

### Frontend

consume solo POST /graphql

### Gateway

enruta /graphql al nuevo servicio BFF
Nuevo microservicio graphql-bff
expone el schema GraphQL
agrega datos llamando a 2 microservicios como mucho

### Resto de microservicios

se quedan tal cual, con REST o gRPC

Con eso se demuestra integración real sin meterse en un refactor gigante.

## Caso de uso MVP más sensato para GameListo

Escogería solo Catálogo + Biblioteca.

Porque permite enseñar:

- una query simple,
- una query agregada entre dos servicios,
- una mutation,
- un pequeño modelo con relaciones anidadas.

### Ejemplo de alcance

game(slug) → datos del catálogo
myGame(slug) → datos del catálogo + estado del usuario en biblioteca
setGameStatus(input) → mutation para cambiar “quiero / jugando / completado”

Con eso ya se enseña lo esencial de GraphQL sin meterse en publicaciones, social, recomendaciones o búsqueda avanzada.

## Qué dejaría fuera del MVP

Para que siga siendo simple, yo no metería:

- subscriptions
- WebSocket
- federation
- pagination compleja
- DataLoader al principio
- GraphQL en todos los servicios
- schema gigante de toda la plataforma

Spring GraphQL soporta transports adicionales, testing avanzado, observabilidad, seguridad fina e incluso federation, pero para un MVP académico básico no hace falta llegar ahí.

## Arquitectura objetivo del MVP

Quedaría así:

Next.js frontend -> Gateway -> graphql-bff -> catálogo + biblioteca

Importante: el gateway no resuelve GraphQL. Solo enruta y aplica concerns transversales.
El que conoce el schema y ejecuta resolvers es el BFF GraphQL.

Eso deja una arquitectura limpia y muy fácil de explicar en la memoria:

- el gateway sigue siendo puerta de entrada,
- GraphQL se usa solo en la capa BFF,
- los microservicios internos no tienen por qué exponerse en GraphQL.
- Plan de implementación por fases

## Fase 1. Crear el microservicio graphql-bff

Crea un servicio Spring Boot independiente con:

- spring-boot-starter-graphql
- un starter web, preferiblemente spring-boot-starter-webflux si quieres mantener una capa edge moderna, o MVC si prefieres simplicidad

Spring Boot indica que necesitas el starter GraphQL y además algún starter web para exponer la API.

Resultado esperado:

- servicio arranca
- expone POST /graphql

## Fase 2. Definir un schema mínimo, schema-first

Spring Boot detecta automáticamente schemas en src/main/resources/graphql/**, así que aprovecha eso y trabaja de forma schema-first.

Con esto demuestras:

- Query
- Mutation
- type
- input
- composición de objetos

Y nada más. Perfecto para un MVP.

## Fase 3. Implementar resolvers con anotaciones de Spring

Spring for GraphQL usa controllers anotados con @Controller, @QueryMapping, @MutationMapping y también @SchemaMapping para campos. Spring Boot los detecta automáticamente y los registra como data fetchers.

Aquí no se necesita inventar nada raro: el resolver delega en un servicio de aplicación del BFF y ese servicio hace las llamadas internas.

## Fase 4. Conectar el BFF con 2 microservicios existentes

Mantén los microservicios internos como estén.

Ya hay REST, usa REST

Lo importante académicamente no es que todo sea GraphQL, sino que el BFF exponga GraphQL al frontend y traduzca esas operaciones a llamadas internas.

## Fase 5. Pasar el gateway a enrutar /graphql

Añade una ruta en Spring Cloud Gateway para que:

/graphql → graphql-bff

Así el frontend no llama al BFF directamente, sino al sistema a través del gateway, que es justo lo coherente con tu arquitectura.

Spring permite introspection por defecto, precisamente para herramientas como GraphiQL, aunque puede desactivarse luego si lo necesitas.

Con eso puedes enseñar en la demo (pendiente de verificar en el dominio):

query {
  game(slug: "elden-ring") {
    title
    coverUrl
    platforms
  }
}

y también:

query {
  myGame(slug: "elden-ring") {
    game {
      title
      releaseYear
    }
    libraryEntry {
      status
      rating
    }
  }
}

y una mutation:

mutation {
  setGameStatus(input: {
    gameId: "123",
    status: "PLAYING",
    rating: 9
  }) {
    gameId
    status
    rating
  }
}

Eso, en una defensa, queda muy bien porque se ve enseguida el valor de GraphQL.

## Fase 7. Añadir manejo básico de errores

GraphQL no responde exactamente igual que un REST clásico: puede devolver data, errors e incluso datos parciales junto con errores. Baeldung explica muy bien este comportamiento, y Spring Boot permite registrar DataFetcherExceptionResolver para resolver excepciones del grafo.

Para el MVP basta con:

- controlar “juego no encontrado”
- controlar “entrada de biblioteca no encontrada”
- controlar input inválido

No hace falta más.

## Fase 8. Añadir 2 o 3 tests de GraphQL

Spring GraphQL incluye GraphQlTester, y Spring Boot soporta pruebas de controllers GraphQL con @GraphQlTest y pruebas de integración HTTP con HttpGraphQlTester.

Los tests mínimos podrían ser:

- query game devuelve datos correctos
- query myGame agrega catálogo + biblioteca
- mutation setGameStatus actualiza el estado

Con eso ya se puede decir que no solo se implementó, sino que además se validó.

## Orden real de trabajo

Yo lo haría en este orden exacto:

1. Crear graphql-bff
2. Exponer /graphql
3. Definir schema mínimo
4. Implementar game(slug)
5. Implementar setGameStatus
6. Implementar myGame(slug) agregando dos servicios
7. Añadir manejo básico de errores
8. Añadir tests
9. Conectar gateway
10. Conectar frontend a una sola query real
