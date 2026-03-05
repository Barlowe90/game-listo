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
   Base path: `/v1/social/users`

- POST /v1/social/users/{userId}/friends/{friendId}
    - Descripción: Crear una amistad (inserta relación entre `userId` y `friendId`).
    - Autorización: requiere usuario autenticado (el gateway gestiona la validación JWT). En desarrollo la seguridad
      puede estar relajada.
    - Respuesta: 200 OK si se creó o ya existe.

- DELETE /v1/social/users/{userId}/friends/{friendId}
    - Descripción: Eliminar la relación de amistad.
    - Respuesta: 204 No Content

- GET /v1/social/users/{userId}/friends
    - Descripción: Listar amigos de `userId`.
    - Respuesta: 200 OK con una lista de objetos { id, username, avatar }

- GET /v1/social/users/{userAId}/friends/common/{userBId}
    - Descripción: Listar amigos en común entre `userAId` y `userBId`.
    - Respuesta: 200 OK

Ejemplo cURL (suponiendo gateway y JWT si procede):

```bash
# Añadir amistad
curl -X POST \
  http://localhost:8085/v1/social/users/123/friends/456 \
  -H "Authorization: Bearer <ACCESS_TOKEN>"

# Listar amigos
curl http://localhost:8085/v1/social/users/123/friends -H "Authorization: Bearer <ACCESS_TOKEN>"
```

2) Eventos consumidos (RabbitMQ)

El microservicio consume eventos desde la cola `social` ligados al exchange `gamelisto.eventos` con routing key
`usuarios.#`.

Eventos gestionados por `SocialListener`:

- `UsuarioCreado` → payload: { usuarioId, username, avatar }
    - Acción: crear un nodo liviano en el grafo para el nuevo usuario.
- `UsuarioEliminado` → payload: { usuarioId }
    - Acción: eliminar el nodo y sus relaciones asociadas.

Configuración de la cola/exchange: ver `RabbitMQConfig` (Queue `social`, Exchange `gamelisto.eventos`, binding
`usuarios.#`).

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

8) Código relevante (ubicaciones rápidas)

- Controlador REST: `infrastructure/in/api/SocialController.java`
- Listeners: `infrastructure/in/messaging/SocialListener.java`
- Rabbit config: `infrastructure/in/messaging/RabbitMQConfig.java`
- Use cases: `application/usecases/*` (AgregarAmigoUseCase, EliminarAmigoUseCase, ListarAmigosUseCase...)
- Domínio (puertos): `dominio/AmistadRepositorio.java`, `dominio/UserRef.java`

Notas finales

- Este microservicio es intencionalmente pequeño y enfocado. Si quieres, puedo generar ejemplos de pruebas de
  integración (test que publica un evento en RabbitMQ y verifica el contenido en Neo4j) y añadirlos al directorio
  `src/test/integration`.

---
