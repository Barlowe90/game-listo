# 📘 Microservicio Social -- GameListo

## 1. Descripción

El microservicio **Social** es responsable de modelar y gestionar el
grafo de relaciones entre usuarios de GameListo.

Se implementa utilizando **Neo4j** como base de datos orientada a
grafos, debido a su eficiencia en: - Recorridos entre nodos
(friends-of-friends) - Consultas de afinidad - Cálculo de amigos
comunes - Consultas "amigos que juegan a X"

Este microservicio **no es fuente de verdad** de usuarios ni
videojuegos. Opera con referencias ligeras (`UsuarioRef`, `GameRef`) y
se alimenta mediante eventos de otros microservicios.

------------------------------------------------------------------------

## 2. Responsabilidades del dominio

### ✔ Solicitudes de amistad

- Envío
- Aceptación
- Rechazo

### ✔ Amistades

- Relación simétrica entre usuarios
- Eliminación manual

### ✔ Bloqueos

- Relación unilateral
- Cancela solicitudes activas
- Elimina amistad existente
- Impide nuevas interacciones

### ✔ Actividad lúdica social

Proyección de juegos jugados por usuario: - Estado del juego

------------------------------------------------------------------------

## 3. Modelo del grafo (Neo4j)

### Nodos

#### User

(:User { id, username, avatar })

#### Game

(:Game { id, name })

------------------------------------------------------------------------

### Relaciones

#### SENT_REQUEST

(u1)-\[:SENT_REQUEST { status, createdAt, respondedAt }\]-\>(u2)

Estados: - PENDING - ACCEPTED - REJECTED

------------------------------------------------------------------------

#### FRIEND

(u1)-\[:FRIEND { since }\]-(u2)

Se materializa automáticamente al aceptar solicitud.

------------------------------------------------------------------------

#### BLOCKED

(u1)-\[:BLOCKED { createdAt }\]-\>(u2)

Reglas: - Elimina amistad si existe - Cancela solicitudes pendientes -
Impide nuevas solicitudes

------------------------------------------------------------------------

#### PLAYS

(u)-\[:PLAYS { state, hours, platform, updatedAt }\]-\>(g)

Propiedades: - state (JUGANDO, COMPLETADO, etc.) - hours - platform -
updatedAt

Se actualiza mediante eventos provenientes de Biblioteca.

------------------------------------------------------------------------

## 4. Eventos consumidos

El microservicio Social se alimenta mediante mensajería asíncrona
(RabbitMQ).

### Desde Usuarios

- UserCreatedEvent
- UserDeletedEvent
- UserUpdatedEvent

### Desde Biblioteca

- GameStateUpdatedEvent
- GameRatedEvent

### Desde Catálogo

- GameCreatedEvent
- GameUpdatedEvent

------------------------------------------------------------------------

## 5. Endpoints expuestos

GET /v1/social/friends/{idUser}\
GET /v1/social/friends/common/{userA}/{userB}\
GET /v1/social/friends/play/{idGame}\
GET /v1/social/stats/{idUser}

POST /v1/social/request\
PATCH /v1/social/request/{idUser}/accept\
PATCH /v1/social/request/{idUser}/reject

DELETE /v1/social/friends/{idUser}

POST /v1/social/block/{idUser}\
DELETE /v1/social/unblock/{idUser}

------------------------------------------------------------------------

## 6. Arquitectura interna

Arquitectura hexagonal:

como publicaciones:

- application
- domain
- infra

------------------------------------------------------------------------

## 7. Justificación del uso de Neo4j

Neo4j es adecuado porque: - El dominio es relacional por naturaleza -
Las consultas requieren múltiples saltos - Las operaciones de
intersección son frecuentes - Permite futura ampliación a
recomendaciones y métricas sociales

------------------------------------------------------------------------
