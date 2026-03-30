# Endpoints por Página (Frontend)

Este documento detalla las llamadas a los diferentes endpoints de los microservicios que realiza cada una de las páginas de vista (`page.tsx`) del frontend. Esto permite analizar la granularidad de las consultas y detectar posibles optimizaciones (ej. uso del BFF GraphQL para agrupar consultas).

## Componentes Globales (Layouts / Header)

_Se ejecutan independientemente de la página en la que se encuentre el usuario._

- **Autenticación (carga inicial y sesión)**:
  - `GET /v1/usuarios/auth/me` - Obtiene la información del usuario autenticado.
- **Barra de Búsqueda (Sugerencias)**:
  - `GET /v1/busquedas/sugerencia` - Autocompletado de juegos mientras se escribe.

---

## Páginas Públicas (`/src/app/(public)`)

### 1. Catálogo (`/catalogo`)

- `GET /v1/catalogo/games` - Obtiene la lista paginada de videojuegos con filtros aplicados (géneros, plataformas, etc.).
- `GET /v1/catalogo/platforms` (Aprox) - Obtiene la lista de plataformas para renderizar los filtros.

### 2. Detalle de Videojuego (`/videojuego/[id]`)

_Esta página agrega información de muchos dominios distintos._

- `GET /v1/catalogo/games/{id}` - Obtiene los detalles principales del juego (título, descripción, portada, metadata).
- `GET /v1/biblioteca/games/{id}/state` - Obtiene el estado actual de ese juego en la biblioteca del usuario logueado (jugando, completado, etc.) y su valoración.
- `GET /v1/publicaciones/game/{id}` - Lista las publicaciones/grupos de juego asociados a este videojuego.
- `GET /v1/social/games/{id}/resumen` - Obtiene el resumen social (amigos que lo están jugando o buscando grupo).
- `GET /v1/biblioteca/lists` - Obtiene las listas personalizadas del usuario para poder añadir el juego mediante el menú de acciones.

### 3. Perfil de Usuario (`/usuario/[id]`)

- `GET /v1/usuarios/{id}` - Obtiene la información del perfil público del usuario.
- `GET /v1/publicaciones/user/{id}` - Muestra las publicaciones creadas por este usuario.
- `GET /v1/biblioteca/user/{id}/games` (o derivados) - Obtiene los juegos de la biblioteca del usuario para mostrarlos en su perfil.
- `GET /v1/social/users/friends` - Compara para visualizar el estado de amistad entre el usuario logueado y el perfil visitado.

---

## Páginas Privadas (`/src/app/(private)`)

### 4. Mi Biblioteca (`/biblioteca`)

- `GET /v1/biblioteca/lists` - Obtiene las listas personalizadas del propio usuario para el sidebar/navegación.
- `GET /v1/biblioteca/states` (o `/users/me/games`) - Obtiene los juegos categorizados por estado (Jugando, Pendientes, Completados).

_Acciones derivadas:_

- `POST /v1/biblioteca/steam/import` - Al usar la acción de importar desde Steam.

### 5. Detalle de Lista Personalizada (`/biblioteca/listas/[listaId]`)

- `GET /v1/biblioteca/lists/{listaId}` - Obtiene la estructura de la lista (título, descripción) y los videojuegos concretos que la conforman.

### 6. Mis Publicaciones (`/mis-publicaciones`)

- `GET /v1/publicaciones/user/{worker_id}` (usuario actual) - Obtiene las publicaciones donde el usuario es creador.
- `GET /v1/publicaciones/solicitudes/recibidas` - Obtiene la bandeja de entrada de usuarios pidiendo unirse a los grupos creados por el usuario.
- `GET /v1/publicaciones/solicitudes/enviadas` - Obtiene el estado de las solicitudes que el usuario actual ha mandado a otros grupos.

---

## Páginas de Autenticación / Registro (`/src/app/...`)

### 7. Iniciar Sesión (`/(auth)/login`)

- `POST /v1/usuarios/auth/login` - Verifica credenciales y devuelve tokens de acceso.

### 8. Registro (`/(public)/registro`)

- `POST /v1/usuarios/auth/register` - Crea un nuevo usuario temporal.

### 9. Verificación de Email (`/(public)/auth/verify-email`)

- `POST /v1/usuarios/auth/verify-email` - Confirma el token del correo.

### 10. Recuperar Contraseña (`/(public)/recuperar-password` y `/reset-password`)

- `POST /v1/usuarios/auth/forgot-password` - Solicita el correo de recuperación.
- `POST /v1/usuarios/auth/reset-password` - Restablece la contraseña mediante el token de recuperación.

---

## Análisis y Conclusiones sobre la Arquitectura actual

Basado en este desglose, se observa que en páginas altamente integradoras como **Detalle de Videojuego** y **Perfil de Usuario**, el frontend necesita hacer peticiones a múltiples microservicios:

- En `/videojuego/[id]` se dispara a `catalogo`, `biblioteca`, `publicaciones` y `social` de manera concurrente o en cascada.
- En `/usuario/[id]` se dispara a `usuarios`, `publicaciones`, `biblioteca` y `social`.

Esto demuestra una fuerte dependencia de la capa de presentación para componer o "hacer el join" de modelos de datos dispersos. Dado que existe un microservicio `graphql-bff` planeado/en desarrollo para el ecosistema, estas páginas son las principales candidatas para consumir un endpoint único GraphQL (`POST /graphql`) que internamente orqueste estos llamados, reduzca los round-trips de red en el cliente, y cumpla el rol genuino de Backend for Frontend.
