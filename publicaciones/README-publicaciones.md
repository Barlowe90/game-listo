# Servicio de Publicaciones (GameListo)

Microservicio responsable de las **publicaciones** relacionadas con videojuegos, orientado a la creación y gestión
de **grupos de juego** (estilo LFG): creación de una publicación para un juego concreto, gestión de solicitudes
de unión y materialización del grupo final cuando se aceptan participantes.

> En GameListo, este servicio es uno de los dominios principales junto con *usuarios, catálogo, biblioteca, búsqueda*
> y *social*.

---

## Responsabilidades

- **Crear / editar / eliminar** publicaciones para un juego
- **Listar publicaciones** por juego y por usuario
- **Gestionar solicitudes de unión** (solicitar, aceptar/rechazar, estado)
- **Gestionar miembros del grupo** (lista de participantes, abandonar grupo)
- Mantener **referencias ligeras** (`UsuarioRef`, `GameRef`) para evitar acoplamiento fuerte con otros microservicios

---

## Visión de dominio

Conceptos principales:

- **Publicación**
    - Post creado por un usuario (`UsuarioRef`) y vinculado a un juego (`GameRef`)
    - Incluye filtros/metadata para emparejar jugadores (idioma, nivel, estilo, capacidad, etc.)
- **Petición de unión**
    - Solicitud de un usuario para unirse a una publicación
    - Ciclo de vida: solicitada → aceptada/rechazada
- **Grupo de juego**
    - Grupo asociado a una publicación cuando se aceptan miembros
    - Punto de anclaje para integraciones externas (chat)

---

## Persistencia

Este servicio almacena datos transaccionales y estructurados (publicaciones, solicitudes, grupos y referencias
ligeras). Entidades relacionales típicas:

- `publicacion`
- `usuario_ref`
- `game_ref`
- `peticion_union`
- `grupo_juego`

> Los nombres exactos de tablas/columnas pueden variar según tu implementación, pero la idea es mantener el servicio
> **autónomo** y **desacoplado** mediante referencias.

---

## API

Base path: `/v1/publicaciones`

### Endpoints (resumen)

| Método             | Ruta                                        | Descripción                                   |
|--------------------|---------------------------------------------|-----------------------------------------------|
| GET / POST         | `/publicaciones`                            | Listar publicaciones / Crear publicación      |
| GET / PUT / DELETE | `/{idPublicacion}`                          | Obtener / Actualizar / Eliminar publicación   |
| GET / POST         | `/{idPublicacion}/solicitud-union`          | Listar solicitudes / Crear solicitud de unión |
| PATCH              | `/{idPublicacion}/solicitud-union/{idUser}` | Aceptar/Rechazar una solicitud                |
| POST               | `/{idPublicacion}/abandonar-grupo`          | Abandonar el grupo                            |
| GET                | `/usuario/{idUsuario}`                      | Listar publicaciones creadas por un usuario   |
| GET                | `/game/{idGame}`                            | Listar publicaciones de un juego              |
| GET                | `/grupos/{idGrupo}`                         | Obtener detalles del grupo                    |
| GET                | `/{idPublicacion}/participantes`            | Listar participantes de una publicación       |

> Mantén las reglas de autorización alineadas con la política de Gateway/Seguridad (p. ej., solo el autor puede
> editar/eliminar; solo miembros pueden abandonar; admin puede tener override).

---

## Seguridad

- Las peticiones deberían entrar **a través del API Gateway**, que valida JWT y aplica políticas transversales
- (CORS, rate limiting, etc.). Dentro del servicio, aplica **autorización** en controladores/casos de uso (p. ej.,
  operaciones solo del autor).

---

## Eventos

Tiene que estar a la escucha de los eventos publicados por usuarios y catalogo.

---

## Desarrollo local

### Requisitos

- Java
- Maven
- Docker + Docker Compose (para BD e infraestructura compartida)

### Ejecutar (ejemplo)

```bash
# desde la carpeta del microservicio
mvn clean test
mvn spring-boot:run
```

### Ejecutar con Docker Compose (ejemplo)

Si tienes un `docker-compose.yml` en la raíz del proyecto con dependencias comunes (BD, RabbitMQ, etc.):

```bash
docker compose up -d
```

---

## Configuración

Tener como referencia usuarios, catalogo y biblioteca

---

## Testing

- Unit tests: dominio + capa de aplicación
- Integration tests: persistencia y capa REST (slice tests / Testcontainers si aplica)
- No utilizar H2, utilizar mongoDB

Ejecutar:

```bash
mvn test
```

---

## Estructura del proyecto (hexagonal)

Estructura típica:

- `domain/` — entidades, value objects, servicios de dominio
- `application/` — casos de uso (commands/queries), puertos
- `infrastructure/`
    - `in/` controladores REST (adaptadores de entrada)
    - `out/` adaptadores de persistencia (JPA), adaptadores de mensajería

---

## Notas

- Prioriza operaciones **idempotentes** para solicitudes de unión y creación de grupos (evita duplicados).
- Ten en cuenta restricciones: capacidad del grupo, publicaciones cerradas y membresías duplicadas.
