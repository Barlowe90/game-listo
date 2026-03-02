# Servicio de Publicaciones (GameListo)

Microservicio responsable de las **publicaciones** relacionadas con videojuegos, orientado a la creación y gestión de *
*grupos de juego** (estilo *LFG / Looking For Group*): creación de una publicación para un juego concreto, gestión de
solicitudes de unión y mantenimiento del grupo de participantes.


> En GameListo, este servicio es uno de los dominios principales junto con *usuarios, catálogo, biblioteca, búsqueda*
> y *social*.

## 1. Qué resuelve este microservicio

Flujo funcional (vista de usuario):

1. El usuario navega a un videojuego y entra en la pestaña **Publicaciones**.
2. Ve un listado con todas las publicaciones de ese juego.
3. Cada publicación muestra su información (autor, descripción, filtros) y **los participantes actuales** (incluyendo el
   autor).
4. Un usuario puede **enviar una solicitud** para unirse a una publicación.
5. El autor de la publicación **acepta o rechaza** la solicitud.
6. Si se acepta, el solicitante pasa a ser **miembro del grupo** y se refleja en la publicación.
7. El autor puede **eliminar** la publicación en cualquier momento (borrado lógico recomendado).

---

## 2. Responsabilidades

- Crear / actualizar / eliminar publicaciones para un juego.
- Listar publicaciones **por juego** y **por usuario (autor)**.
- Gestionar solicitudes de unión: crear, cancelar, aceptar/rechazar.
- Gestionar miembros del grupo: ver participantes y abandonar grupo.
- Mantener **referencias ligeras**:
    - `UsuarioRef` (datos mínimos del autor/participantes)
    - `GameRef` (datos mínimos del juego)
      para evitar acoplamiento fuerte con otros microservicios.

---

## 3. Visión de dominio (DDD)

### 3.1 Agregados y entidades principales

- **Publicación**
    - Post creado por un usuario (`UsuarioRef`) y vinculado a un juego (`GameRef`).
    - Contiene metadata para emparejar jugadores (idioma, plataforma, nivel, estilo, etc.).
    - Tiene un estado (publicada / eliminada).
- **Petición de unión**
    - Solicitud de un usuario para unirse a una publicación.
    - Está asociada a una **publicación** y a un **usuario solicitante**.
- **Grupo de juego**
    - Conjunto de miembros participantes asociado a una publicación.
    - Puede existir desde la creación de la publicación (recomendado para simplificar) con el autor como miembro
      inicial.

### 3.2 Estados y ciclo de vida

**Estado de Petición**

```java
public enum EstadoPeticion {
    SOLICITADA,
    CANCELADA,
    ACEPTADA,
    RECHAZADA
}
```

Ciclo de vida típico:

- `SOLICITADA → ACEPTADA`
- `SOLICITADA → RECHAZADA`
- `SOLICITADA → CANCELADA`

> Importante: **sí se persiste** la petición desde que está `SOLICITADA` (tabla `peticion_union`). El estado se
> actualiza según la acción.

**Estado de Publicación**

```java
public enum EstadoPublicacion {
    PUBLICADA,
    ELIMINADA
}
```

Recomendación:

- `ELIMINADA` como **borrado lógico**: no se devuelve en listados normales, pero se mantiene el registro para
  auditoría / consistencia / troubleshooting.

### 3.3 Reglas de negocio (invariantes)

- Solo el **autor** puede:
    - editar/eliminar la publicación,
    - aceptar/rechazar solicitudes.
- Un usuario **no puede**:
    - solicitar unirse a su propia publicación,
    - solicitar unirse si ya es miembro,
    - tener más de una petición activa para la misma publicación (idempotencia).
- Capacidad:
    - si el grupo está completo, no se pueden aceptar nuevas solicitudes.
- Abandono:
    - un miembro puede abandonar el grupo; si el autor abandona, eliminar publicación.

---

## 4. Persistencia

Este servicio almacena datos transaccionales y estructurados: publicaciones, solicitudes, grupos y referencias ligeras.

**Tecnología recomendada:** PostgreSQL (JPA/Hibernate) para consistencia y transacciones.

Entidades/tables típicas:

- `publicacion`
- `peticion_union`
- `grupo_juego`
- `grupo_juego_miembro` (si modelas membresía como tabla intermedia)
- `usuario_ref`
- `game_ref`

Relaciones orientativas:

- `publicacion (1) -> (1) grupo_juego`
- `publicacion (1) -> (N) peticion_union`
- `grupo_juego (1) -> (N) miembros`
- `publicacion (N) -> (1) game_ref`
- `publicacion (N) -> (1) usuario_ref` (autor)

> Los nombres exactos pueden variar según tu implementación. La idea clave es mantener el servicio **autónomo** y
> desacoplado mediante referencias.

---

## 5. API REST

**Base path:** `/v1/publicaciones`

### 5.1 Convenciones

- IDs: preferentemente `UUID`.
- Autenticación: **Bearer JWT** (validado normalmente en Gateway).
- Autorización: además del Gateway, aplica reglas en el servicio (p.ej. `@PreAuthorize`).
- Paginación: recomendable en listados por juego/usuario.

### 5.2 Endpoints (resumen)

| Método | Ruta                                        | Descripción                                 | Quién puede            |
|--------|---------------------------------------------|---------------------------------------------|------------------------|
| GET    | `/game/{gameId}`                            | Listar publicaciones de un juego            | Público                |
| GET    | `/usuario/{userId}`                         | Listar publicaciones creadas por un usuario | Público                |
| POST   | `/`                                         | Crear publicación                           | Autenticado            |
| GET    | `/{publicacionId}`                          | Obtener detalle de una publicación          | Público                |
| PUT    | `/{publicacionId}`                          | Actualizar publicación                      | **Autor**              |
| DELETE | `/{publicacionId}`                          | Eliminar publicación (lógico)               | **Autor**              |
| GET    | `/{publicacionId}/solicitud-union`          | Listar solicitudes de unión                 | **Autor**              |
| POST   | `/{publicacionId}/solicitud-union`          | Crear solicitud de unión                    | Autenticado (no autor) |
| PATCH  | `/{publicacionId}/solicitud-union/{userId}` | Aceptar/Rechazar solicitud                  | **Autor**              |
| POST   | `/{publicacionId}/abandonar-grupo`          | Abandonar grupo                             | Miembro autenticado    |

> Nota: en `PATCH .../{userId}` se asume que hay **una solicitud por usuario** para esa publicación. Si en tu diseño hay
`solicitudId`, es más robusto usarlo en la ruta.

### 5.3 Ejemplos de flujo (alto nivel)

**A) Crear publicación**

1. `POST /v1/publicaciones`
2. Se crea `publicacion` + (recomendado) `grupo_juego` con autor como primer miembro.

**B) Solicitar unirse**

1. `POST /v1/publicaciones/{id}/solicitud-union`
2. Se crea `peticion_union` en estado `SOLICITADA` (idempotente si ya existe una activa).

**C) Aceptar solicitud**

1. `PATCH /v1/publicaciones/{id}/solicitud-union/{userId}` con acción `ACEPTAR`
2. Cambia estado a `ACEPTADA` y se añade miembro al grupo.

**D) Rechazar o cancelar**

- Rechazo por el autor: estado `RECHAZADA`
- Cancelación por solicitante: estado `CANCELADA` (si lo implementas, puede ser `DELETE` o `PATCH`)

---

## 6. Seguridad

- Las peticiones deberían entrar **a través del API Gateway** (validación JWT + políticas transversales como
  CORS/rate-limit).
- Dentro del microservicio aplica autorización de negocio:
    - solo autor puede modificar publicación o gestionar solicitudes,
    - solo miembro puede abandonar grupo,
    - admin puede tener override.

---

## 7. Eventos e integración entre microservicios

### 7.1 Eventos consumidos (listener)

Este servicio debe escuchar eventos de:

- **Usuarios**: cuando se crea/actualiza un usuario, actualizar `usuario_ref`.
- **Catálogo**: cuando se crea/actualiza un juego, actualizar `game_ref`.

Objetivo: poder mostrar información mínima sin llamadas síncronas a otros servicios.

### 7.2 Eventos publicados (opcional, recomendado)

Para desacoplar e integrar con otros dominios:

- `PublicacionCreada`, `PublicacionActualizada`, `PublicacionEliminada`
- `SolicitudUnionCreada`, `SolicitudUnionResuelta` (aceptada/rechazada/cancelada)
- `MiembroGrupoAnadido`, `MiembroGrupoEliminado`

> Si solo necesitas el microservicio para el TFG, puedes publicar el mínimo imprescindible y documentarlo aquí.

---

## 8. Desarrollo local

### 8.1 Requisitos

- Java (recomendado 21 si el resto del proyecto va alineado)
- Maven
- Docker + Docker Compose (BD y mensajería)

### 8.2 Ejecutar

```bash
# desde la carpeta del microservicio
mvn clean test
mvn spring-boot:run
```

### 8.3 Ejecutar infraestructura con Docker Compose

Si existe un `docker-compose.yml` en la raíz del proyecto (PostgreSQL, RabbitMQ, etc.):

```bash
docker compose up -d
```

---

## 9. Testing

Recomendación para un TFG sólido y simple (KISS):

- **Unit tests**: dominio + casos de uso (sin Spring).
- **Integration tests**: REST + persistencia real con **Testcontainers (PostgreSQL)**.
- Evitar H2 si quieres asegurar compatibilidad real con Postgres (tipos, constraints, consultas).

Ejecutar:

```bash
mvn test
```

---

## 10. Estructura del proyecto (hexagonal)

Estructura típica:

- `domain/` — entidades, value objects, servicios de dominio
- `application/` — casos de uso (commands/queries), puertos (interfaces)
- `infrastructure/`
    - `in/` adaptadores de entrada (REST controllers, listeners)
    - `out/` adaptadores de salida (JPA repositories, mensajería)

---

## 11. Notas de implementación

- Prioriza operaciones **idempotentes** para:
    - crear solicitudes de unión,
    - añadir miembros al grupo.
- Controla condiciones de carrera: aceptar 2 solicitudes a la vez cuando queda 1 hueco (optimistic lock o constraint).
- Si usas borrado lógico, filtra `EstadoPublicacion=PUBLICADA` en listados.
