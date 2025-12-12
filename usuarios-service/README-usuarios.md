# Microservicio de Usuarios – GameListo

Microservicio responsable de la **gestión de cuentas y perfiles de usuario** dentro del ecosistema GameListo.  
Implementa el ciclo de vida completo del usuario: registro, verificación de email, gestión de perfil, cambio y restablecimiento de contraseña, e integración con Discord.

> **Nota:** Este microservicio gestiona la verificación de email y restablecimiento de contraseña mediante tokens.  
El `auth-service` se encargará de la gestión de sesiones y emisión de JWT.

## Descripción del proyecto

El `usuarios-service` forma parte de una plataforma social de videojuegos donde los usuarios pueden gestionar su biblioteca, crear listas personalizadas y conectar con otros jugadores. Este microservicio es el **aggregate root** para toda la información del usuario.

### Responsabilidades principales

- **Registro y verificación**: Creación de cuentas con verificación obligatoria por email (tokens de 24h)
- **Gestión de perfil**: Username, avatar, idioma, preferencias de notificación
- **Seguridad de cuenta**: Cambio y restablecimiento de contraseña
- **Integración Discord**: Vinculación/desvinculación de cuentas
- **Ciclo de vida**: Estados `PENDIENTE_DE_VERIFICACION` → `ACTIVO` / `SUSPENDIDO` / `ELIMINADO`

### Tecnologías

- **Spring Boot 3.5.x** con **Java 21**
- **PostgreSQL** (producción) / **H2** (desarrollo)
- **DDD + Arquitectura Hexagonal**
- **BCrypt** para hashing de contraseñas

---

## 📌 Responsabilidades del microservicio

### ✔️ Funcionalidades principales

- **Registro de usuarios** con generación automática de token de verificación
- **Verificación de email** mediante token con expiración (24h)
- **Reenvío de verificación** si el token ha expirado
- **Restablecimiento de contraseña** con token temporal
- **Cambio de contraseña** validando la contraseña actual
- Consulta del perfil propio y perfiles públicos
- Edición de datos del perfil:
  - `username`, `email`, `avatar`
  - `language` (idioma preferido)
  - `notificationsActive` (preferencias de notificación)
- **Vinculación con Discord** (OAuth2 callback)
- Gestión del estado del usuario: `PENDIENTE_DE_VERIFICACION`, `ACTIVO`, `SUSPENDIDO`, `ELIMINADO`
- Listado y búsqueda de usuarios

### ❌ Funciones que NO realiza

- Login/Logout (sesiones)
- Emisión de JWT
- OAuth2 flow completo (solo callback de Discord)
- Gestión de permisos a nivel de aplicación

---

## 🧱 Arquitectura

El microservicio implementa **DDD + Arquitectura Hexagonal (Ports & Adapters)**.

```text
/domain
/application
/infrastructure
/shared
```

---

## 🗃️ Modelo de datos

### Entidad: Usuario (Aggregate Root)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `UsuarioId` (UUID) | Identificador único del usuario |
| `username` | `Username` | Nombre de usuario único (3-30 caracteres alfanuméricos) |
| `email` | `Email` | Correo electrónico único normalizado |
| `passwordHash` | `PasswordHash` | Hash BCrypt de la contraseña |
| `avatar` | `Avatar` | URL del avatar (opcional) |
| `createdAt` | `Instant` | Fecha de creación |
| `updatedAt` | `Instant` | Fecha de última actualización |
| `role` | `Rol` | Rol del usuario: `USER`, `ADMIN`, `MODERATOR` |
| `language` | `Idioma` | Idioma preferido: `ESP`, `ENG` |
| `notificationsActive` | `boolean` | Preferencia de notificaciones |
| `status` | `EstadoUsuario` | Estado de la cuenta |
| `discordUserId` | `DiscordUserId` | ID de Discord vinculado |
| `discordUsername` | `DiscordUsername` | Username de Discord |
| `discordLinkedAt` | `Instant` | Fecha de vinculación con Discord |
| `discordConsent` | `boolean` | Consentimiento para vinculación Discord |
| `tokenVerificacion` | `TokenVerificacion` | Token UUID para verificación/reset |
| `tokenVerificacionExpiracion` | `Instant` | Expiración del token (24h) |

### Estados de Usuario (`EstadoUsuario`)

| Estado | Descripción |
|--------|-------------|
| `PENDIENTE_DE_VERIFICACION` | Usuario recién registrado, esperando verificación de email |
| `ACTIVO` | Usuario verificado y activo |
| `SUSPENDIDO` | Usuario temporalmente suspendido |
| `ELIMINADO` | Usuario eliminado (soft delete) |

### Value Objects

- `UsuarioId` – UUID inmutable
- `Username` – 3-30 caracteres alfanuméricos
- `Email` – Normalizado a minúsculas, máx. 255 caracteres
- `PasswordHash` – Hash BCrypt válido
- `Avatar` – URL válida o vacío
- `DiscordUserId` – ID numérico de Discord
- `DiscordUsername` – Usuario#discriminador de Discord
- `TokenVerificacion` – UUID para verificación y reset de contraseña

---

## 🔌 Endpoints

Base path: `/v1/usuarios`

### Health Check

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/health` | Estado del servicio |

### Autenticación / Registro

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/auth/register` | Registro de nuevo usuario |
| POST | `/auth/verify-email` | Verificar email con token |
| POST | `/auth/resend-verification` | Reenviar email de verificación |
| POST | `/auth/reset-password` | Restablecer contraseña con token |
| POST | `/auth/discord/link/callback` | Callback OAuth2 de Discord |
| DELETE | `/auth/discord/link/callback` | Callback OAuth2 de Discord |

### Gestión de Usuarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/users` | Listar todos los usuarios |
| GET | `/user/{idUser}` | Obtener usuario por ID |
| PATCH | `/user/{idUser}` | Editar perfil del usuario |
| DELETE | `/user/{idUser}` | Eliminar usuario (soft delete) |
| POST | `/user/{idUser}/change-password` | Cambiar contraseña |
| PATCH | `/user/{idUser}/state` | Cambiar estado del usuario |

---

## 🔐 Seguridad

- Requiere JWT (validado en Gateway)
- Un usuario solo puede editar su propio perfil
- Admins pueden suspender cuentas

---

## 🧪 Testing

- Unit tests del dominio
- Tests de casos de uso con mocks
- Tests de integración (REST + JPA)

---

## 🚀 Ejecución local

```bash
#!/bin/bash
./mvnw spring-boot:run
```

Base de datos:

```bash
#!/bin/bash
docker compose up -d
```

---

## 📝 Licencia

Proyecto académico del TFG GameListo.
