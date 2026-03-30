# Microservicio Social — GameListo

Este README describe el microservicio `social` (gestión del grafo de amistades) y contiene la información necesaria para
desarrollar, ejecutar y depurar el servicio.

Estado y propósito

- Propósito: mantener el grafo social (amistades, operaciones de consultas relacionadas) y consumir eventos del servicio
  `usuarios` para sincronizar nodos.
- Nota: `social` NO es la fuente de verdad de usuarios; mantiene referencias ligeras (`UserRef`) y procesa eventos (
  UsuarioCreado/UsuarioEliminado).

Tecnologías principales

- Java 21, Spring Boot 4.0.0
- Neo4j (grafo) — adaptadores de persistencia en `infrastructure/persistence` (si existen)
- RabbitMQ para ingestión de eventos (configuración en `infrastructure/in/messaging`)

Contenido del README

1. Endpoints HTTP
2. Eventos consumidos (RabbitMQ)
3. Configuración y variables de entorno
4. Iniciar localmente
5. Desarrollo y pruebas unitarias
6. Debugging y notas operativas

1) Endpoints HTTP
   Base path: `/v1/social`

| Método | Ruta                                  | Auth / Rol    | Request | Response                              | Descripción / Notas                                                                                                                                                                              |
|--------|---------------------------------------|---------------|---------|---------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| POST   | `/v1/social/users/friends/{friendId}` | Authenticated | —       | void (200 OK)                         | Añadir amigo: crea relación entre usuario autenticado (`userId` extraído vía `@AuthenticationPrincipal`) y `friendId` (`UUID`). El controlador ejecuta `agregarAmigo.execute(userId, friendId)`. |
| DELETE | `/v1/social/users/friends/{friendId}` | Authenticated | —       | void (204 No Content)                 | Eliminar amistad entre usuario autenticado y `friendId` (`UUID`) mediante `eliminarAmigo.execute(userId, friendId)`.                                                                             |
| GET    | `/v1/social/users/friends`            | Authenticated | —       | `List<UsuarioRefResponse>` (200 OK)   | Listar amigos del usuario autenticado. El controlador mapea `UserRefResult` a `UsuarioRefResponse(id, username, avatar)`.                                                                        |
| GET    | `/v1/social/games/{gameId}/summary`   | Authenticated | —       | `ResumenSocialJuegoResponse` (200 OK) | Obtener resumen social de un juego para el usuario autenticado. El controlador declara `@PathVariable Long gameId` y llama a `consultarResumenSocialJuego.execute(userId, gameId)`.              |

Notas:

- En el controlador `userId` se extrae mediante `@AuthenticationPrincipal` (no se pasa como path param). Los endpoints
  que requieren autenticación esperan un principal de tipo `UUID`.
- Tipos: `friendId` se declara como `UUID` en los controladores (`@PathVariable UUID friendId`). `gameId` es `Long` (
  `@PathVariable Long gameId`).
- Códigos HTTP: `POST /users/friends/{friendId}` devuelve 200 OK; `DELETE` devuelve 204 No Content; los `GET` devuelven
  200 OK con payload.
- Implementación: los controladores son delgados y delegan en use case handlers (`AgregarAmigoHandle`,
  `EliminarAmigoHandle`, `ListarAmigosHandle`, `ConsultarResumenSocialJuegoHandle`).
- Seguridad: el servicio asume que la autenticación se realiza en el API Gateway y que se propaga un principal válido.
  Localmente puedes deshabilitar seguridad o configurar Spring Security para proporcionar un `UUID` como principal.

2) Eventos consumidos (RabbitMQ)

El microservicio consume eventos desde la cola `social` ligados al exchange `gamelisto.eventos` con routing key
`usuarios.#`.

Eventos gestionados por `SocialListener`:

- `UsuarioCreado` → payload: { usuarioId, username, avatar }
    - Acción: crear un nodo liviano en el grafo para el nuevo usuario.
- `UsuarioEliminado` → payload: { usuarioId }
    - Acción: eliminar el nodo y sus relaciones asociadas.

3) Configuración y variables de entorno
   Revisa `src/main/resources/application*.properties` y `src/main/resources/application-docker.properties`.
   Variables importantes (ejemplos):

- `spring.neo4j.uri` — URI de conexión a Neo4j (bolt://localhost:7687)
- `spring.neo4j.authentication.username` / `spring.neo4j.authentication.password`
- `messaging.rabbitmq.enabled` — habilita listeners (true/false)
- `spring.rabbitmq.host` / `spring.rabbitmq.port` / `spring.rabbitmq.username` / `spring.rabbitmq.password`

Asegúrate de que los valores en el `.env` / docker-compose coinciden con los expuestos aquí.

4) Iniciar localmente

Requisitos:

- Java 21
- Maven (o usa el wrapper `mvnw` / `mvnw.cmd`)
- Neo4j en ejecución (docker-compose o instalación local)
- RabbitMQ en ejecución (docker-compose o local)

Arranque rápido con Docker Compose (desde la raíz del repo):

```powershell
# Levantar infra básica
docker-compose up -d neo4j rabbitmq
```

Arrancar el servicio `social` desde el IDE o terminal:

```powershell
cd social
# Windows
.\mvnw.cmd clean install -DskipTests; .\mvnw.cmd spring-boot:run
```

Si quieres ejecutar sólo pruebas unitarias:

```powershell
cd social
.\mvnw.cmd test
```

5) Desarrollo y pruebas

- Tests unitarios: el módulo contiene pruebas en `src/test/java`. Ejecuta `mvn test` en la carpeta `social`.
- Patrón de código: Use Cases en `application/usecases`, puerto `AmistadRepositorio` en `dominio`, adaptadores en
  `infrastructure`.
- Añadir nuevos casos de uso: crear interfaz Handle en `application/usecases`, implementación en `usecases` y exponer en
  controlador si procede.

6) Debugging y notas operativas

- Logs: revisar salida del servicio y `docker-compose logs -f neo4j rabbitmq social`.
- RabbitMQ: usa la UI de management (por defecto http://localhost:15672) para inspeccionar exchanges/queues y mensajes.
  Verifica que el exchange `gamelisto.eventos` existe y tiene binding a la cola `social`.
- Eventos sin header `eventType`: el listener los ignorará (ver `SocialListener`). Asegura que el productor añade el
  header `eventType`.
- JWT/Autorización: el microservicio espera que la autenticación se realice en el gateway; localmente puedes
  deshabilitar seguridad o enviar tokens válidos según `gateway`.
- Neo4j: si los nodos no aparecen, revisa la configuración `spring.neo4j.uri` y credenciales; comprueba también el
  schema si la implementación del repositorio la requiere.

7) Contratos y pruebas de integración mínimas recomendadas

- Prueba que al publicar `UsuarioCreado` con header `eventType=UsuarioCreado` en exchange `gamelisto.eventos`, el
  servicio reciba y cree el nodo.
- Prueba endpoints REST con usuarios ya creados en Neo4j.

---
