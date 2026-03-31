# Microservicio de Usuarios – GameListo

TODO todavía tengo que editar este archivo ya que se han hecho cambios posteriores, no hacerle caso a este archivo

Microservicio responsable de la **gestión de cuentas, perfiles de usuario y autenticación JWT** dentro del ecosistema
GameListo.  
Implementa el ciclo de vida completo del usuario: registro, verificación de email, gestión de perfil, cambio y
restablecimiento de contraseña, y generación de tokens de acceso.

> **Nota:** Este microservicio gestiona la verificación de email, restablecimiento de contraseña mediante tokens con
> expiración de 24 horas, y **generación de tokens JWT** para autenticación.  
> La **validación de tokens JWT** en las peticiones es responsabilidad del **API Gateway** (Spring Cloud Gateway), que
> verifica firma, expiración, claims y controla el acceso a rutas públicas/protegidas.
>
> **Separación de responsabilidades**: Este servicio **genera** los tokens JWT en `/auth/login` y `/auth/refresh`, pero
> el **API Gateway** los valida en todas las demás peticiones. El Gateway envía headers confiables (`X-User-Id`,
`X-User-Roles`) que
> este servicio usa sin validar el JWT nuevamente.

## Tabla de Contenidos

- [Descripción del proyecto](#descripción-del-proyecto)
- [Responsabilidades del microservicio](#responsabilidades-del-microservicio)
- [Arquitectura](#arquitectura)
- [Modelo de datos](#modelo-de-datos)
- [Endpoints](#endpoints)
- [Seguridad](#seguridad)
- [Testing](#testing)
- [Ejecución local](#ejecución-local)
- [Variables de entorno](#variables-de-entorno)
- [Convenciones de código](#convenciones-de-código)

## Descripción del proyecto

El `usuarios` forma parte de una plataforma social de videojuegos donde los usuarios pueden gestionar su
biblioteca, crear listas personalizadas y conectar con otros jugadores. Este microservicio es el **aggregate root** para
toda la información del usuario.

### Responsabilidades principales

- **Registro y verificación**: Creación de cuentas con verificación obligatoria por email (tokens con expiración de 24h)
- **Autenticación JWT**:
    - Login con generación de access token (JWT) y refresh token
    - Rotación de tokens con refresh endpoint
    - Logout con revocación de refresh tokens
    - Consulta de perfil autenticado con token
- **Gestión de perfil**: Username único, avatar, idioma preferido y datos de Discord
- **Seguridad de cuenta**: Cambio de contraseña (con validación), restablecimiento mediante token temporal
- **Ciclo de vida**: Estados `PENDIENTE_DE_VERIFICACION` → `ACTIVO` / `SUSPENDIDO` / `ELIMINADO`
- **Roles de usuario**: `USER`, `ADMIN`

> **Separación de responsabilidades**: Este servicio **genera** tokens JWT, pero **NO los valida** en peticiones
> entrantes. La validación es responsabilidad del API Gateway. El Gateway envía headers de confianza (`X-User-Id`,
> etc.) que este servicio usa sin validar el JWT nuevamente.

### Stack tecnológico

- **Java 21** con características modernas (records, pattern matching, virtual threads ready)
- **Spring Boot 4.0.3** (Spring Framework 6.2.1)
- **Base de datos**: PostgreSQL 17 (producción)
- **Spring Data JPA** con Hibernate 6.6
- **Spring Security** con BCrypt (strength: 10)
- **Maven** para gestión de dependencias
- **Docker & Docker Compose** para orquestación
- **Arquitectura Hexagonal** con **Domain-Driven Design (DDD)**

---

## Responsabilidades del microservicio

### Funcionalidades implementadas

#### Registro y verificación

- **Registro de usuarios** (`POST /v1/usuarios/auth/register`)
    - Creación de cuenta con validación de datos
    - Generación automática de token de verificación (UUID)
    - Hash seguro de contraseña con BCrypt
    - Estado inicial: `PENDIENTE_DE_VERIFICACION`

- **Verificación de email** (`POST /v1/usuarios/auth/verify-email`)
    - Validación de token (expiración 24h)
    - Cambio de estado a `ACTIVO`
    - Limpieza automática de tokens usados

- **Reenvío de verificación** (`POST /v1/usuarios/auth/resend-verification`)
    - Genera nuevo token si el anterior expiró
    - Actualiza fecha de expiración

#### Gestión de contraseña

- **Cambio de contraseña** (`PUT /v1/usuarios/password`)
    - Validación de contraseña actual
    - Hashing de nueva contraseña
    - Usuario autenticado puede cambiar su propia contraseña (se valida
      `@PreAuthorize("#userId == authentication.principal")`)

- **Restablecimiento de contraseña** (`POST /v1/usuarios/auth/reset-password`)
    - Solicitud de reset genera token temporal
    - Validación de token con expiración
    - Actualización segura de contraseña

#### Gestión de perfil

- **Consulta de perfil** (`GET /v1/usuarios/{id}`)
    - Datos públicos del usuario

- **Edición de perfil** (`PATCH /v1/usuarios`)
    - Actualización de `username` (validación de unicidad)
    - Actualización de `email` (requiere nueva verificación)
    - Cambio de `avatar` (URL)
    - Configuración de `language` (`ESP`, `ENG`)

- **Listado de usuarios** (`GET /v1/usuarios/admin/users`)
    - Paginación y filtrado
    - Solo administradores (o según la lógica del controlador) pueden listar todos; hay soporte para búsqueda por
      `username` y filtrado por `estado`

#### Enlazar Discord

- **Añadir datos de Discord** (`PUT /v1/usuarios/discord`)
    - Almacenamiento manual de `discordUserId` para el usuario autenticado
    - Registro de fecha de vinculación

- **Eliminar datos de Discord** (`DELETE /v1/usuarios/discord`)
    - Eliminación de datos de Discord del perfil del usuario autenticado

#### Autenticación JWT

- **Login** (`POST /v1/usuarios/auth/login`)
    - Validación de credenciales (email/username + password)
    - Verificación de estado del usuario (debe estar ACTIVO)
    - Generación de access token (JWT) con claims: `userId`, `username`, `email`, `role`
    - Generación de refresh token (UUID almacenado en BD)
    - Respuesta con ambos tokens

- **Refresh Token** (`POST /v1/usuarios/auth/refresh`)
    - Validación del refresh token
    - Revocación del refresh token anterior
    - Generación de nuevo access token y nuevo refresh token
    - Rotación de tokens para mayor seguridad

- **Logout** (`POST /v1/usuarios/auth/logout`)
    - Revocación del refresh token activo
    - Invalidación de sesión

- **Obtener perfil autenticado** (`GET /v1/usuarios/auth/me`)
    - Extrae userId del access token (validado por Gateway)
    - Retorna datos completos del usuario autenticado

> **Nota**: Los access tokens son JWT firmados con HS256. El API Gateway valida estos tokens en cada petición protegida.

#### Administración

- **Cambio de estado** (`PATCH /v1/usuarios/admin/{id}/estado`)
    - Suspender/reactivar usuarios
    - Eliminación lógica (soft delete)
    - Solo administradores (endpoint con prefijo `/admin`)

- **Eliminación de usuario** (`DELETE /v1/usuarios/admin/{id}`)
    - Soft delete (estado → `ELIMINADO`)
    - Preserva datos para auditoría

> Nota: en el código de pruebas y en documentación previa hay referencias a `GET /v1/usuarios/users` (sin `/admin`). El
> controlador actual expone `GET /v1/usuarios/admin/users`. Si se desea compatibilidad con la ruta sin `/admin`, hay que
> añadir un alias en el controlador o actualizar los tests/documentación según convenga.

### Funcionalidades que NO gestiona

Este microservicio NO es responsable de:

- **Validación de tokens JWT en peticiones** (responsabilidad del API Gateway)
- **Control de acceso a rutas** (el Gateway decide qué rutas son públicas/protegidas)
- **Verificación de firma/expiración de tokens** en requests (delegado al Gateway)
- Gestión de permisos granulares por recurso
- Envío físico de emails (delegado a `IEmailService` port)

> **Separación de responsabilidades**: Este servicio **genera** los tokens JWT en `/auth/login` y `/auth/refresh`, pero
> el **API Gateway** los valida en todas las demás peticiones.

---

## Arquitectura

El microservicio implementa **DDD (Domain-Driven Design)** con **Arquitectura Hexagonal (Ports & Adapters)**.

### Estructura de capas

TODO todavía estoy trabajando en ello

### Regla de dependencias

```text
infrastructure → application → domain
```

**Nunca al revés.** El dominio es completamente independiente de frameworks.

### Patrones implementados

#### 1. Value Objects (Inmutables)

Todos los primitivos del dominio son Value Objects con validación:

- `UsuarioId`, `Username`, `Email`, `PasswordHash`
- `Avatar`, `DiscordUserId`
- `TokenVerificacion`

#### 2. Aggregate Root (`Usuario`)

- Constructor privado + métodos factory (`create()`, `reconstitute()`)
- Métodos de negocio públicos (`changeUsername()`, `verifyEmail()`, etc.)
- Invariantes protegidas

#### 3. Repository Pattern

- Interfaces en `/domain/repositories` con tipos de dominio
- Implementaciones en `/infrastructure/persistence`
- Anti-Corruption Layer con mappers (`UsuarioMapper`)

#### 4. Use Cases (Single Responsibility)

Cada caso de uso es una clase `@Service` con un único método `execute()`:

**Gestión de usuarios:**

- `CrearUsuarioUseCase`
- `VerificarEmailUseCase`
- `EditarPerfilUsuarioUseCase`
- `CambiarContrasenaUseCase`
- `RestablecerContrasenaUseCase`

**Autenticación:**

- `LoginUseCase` - Valida credenciales y genera tokens
- `RefreshTokenUseCase` - Rota refresh token y genera nuevo access token
- `LogoutUseCase` - Revoca refresh token
- `ObtenerPerfilAutenticadoUseCase` - Obtiene perfil desde token JWT
- `RestablecerContrasenaUseCase`
- `ObtenerUsuarioPorId`
- `ObtenerTodosLosUsuariosUseCase`
- `EliminarUsuarioUseCase`
- `CambiarEstadoUsuarioUseCase`
- `ReenviarVerificacionUseCase`

#### 5. Dependency Inversion

Puertos (interfaces) en `application/ports`:

- `IEmailService` → Implementación en `infrastructure/email`
- Futuros: `IStorageService`

---

## Modelo de datos

### Entidad: Usuario (Aggregate Root)

#### Campos principales

| Campo          | Tipo               | Restricciones      | Descripción                    |
|----------------|--------------------|--------------------|--------------------------------|
| `id`           | `UsuarioId` (UUID) | NOT NULL, PK       | Identificador único inmutable  |
| `username`     | `Username`         | UNIQUE, 3-30 chars | Nombre de usuario alfanumérico |
| `email`        | `Email`            | UNIQUE, max 255    | Email normalizado a minúsculas |
| `passwordHash` | `PasswordHash`     | NOT NULL           | Hash BCrypt (strength: 10)     |
| `avatar`       | `Avatar`           | nullable           | URL del avatar del usuario     |

#### Configuración de usuario

| Campo    | Tipo            | Default                     | Descripción         |
|----------|-----------------|-----------------------------|---------------------|
| `role`   | `Rol`           | `USER`                      | Rol del usuario     |
| `status` | `EstadoUsuario` | `PENDIENTE_DE_VERIFICACION` | Estado de la cuenta |

#### Datos de Discord

| Campo           | Tipo            | Descripción                                           |
|-----------------|-----------------|-------------------------------------------------------|
| `discordUserId` | `DiscordUserId` | ID de Discord proporcionado por el usuario (nullable) |

#### Sistema de tokens de verificación

| Campo                         | Tipo                       | Descripción                              |
|-------------------------------|----------------------------|------------------------------------------|
| `tokenVerificacion`           | `TokenVerificacion` (UUID) | Token para verificación/reset (nullable) |
| `tokenVerificacionExpiracion` | `Instant`                  | Fecha de expiración del token (24h)      |

### Entidad: RefreshToken (Aggregate)

Gestiona los tokens de refresco para autenticación JWT.

| Campo       | Tipo                    | Restricciones    | Descripción                   |
|-------------|-------------------------|------------------|-------------------------------|
| `id`        | `RefreshTokenId` (UUID) | NOT NULL, PK     | Identificador único del token |
| `token`     | `TokenValue` (UUID)     | UNIQUE, NOT NULL | Valor del refresh token       |
| `usuarioId` | `UsuarioId` (UUID)      | NOT NULL, FK     | Usuario propietario del token |
| `expiresAt` | `Instant`               | NOT NULL         | Fecha de expiración           |
| `createdAt` | `Instant`               | NOT NULL         | Fecha de creación             |
| `revoked`   | `boolean`               | default: false   | Si el token ha sido revocado  |

### Enumeraciones

#### EstadoUsuario

- PENDIENTE_DE_VERIFICACION — Usuario registrado, email no verificado
- ACTIVO — Usuario verificado y operativo
- SUSPENDIDO — Usuario temporalmente deshabilitado
- ELIMINADO — Soft delete (auditoría)

#### Rol

- USER — Usuario estándar (default)
- ADMIN — Administrador del sistema
- MODERATOR — Moderador de contenido

#### Idioma

- ESP — Español
- ENG — Inglés

### Value Objects del dominio

Todos los VOs implementan validación en construcción y son inmutables:

**Usuario:**

- **UsuarioId**: Wrapper de UUID
- **Username**: 3-30 caracteres alfanuméricos, único
- **Email**: Formato válido, normalizado, máx 255 caracteres, único
- **PasswordHash**: Validación de formato BCrypt (`$2a$` o `$2b$`)
- **Avatar**: URL válida o cadena vacía
- **DiscordUserId**: ID numérico de Discord
- **TokenVerificacion**: UUID aleatorio

**RefreshToken:**

- **RefreshTokenId**: Wrapper de UUID
- **TokenValue**: UUID aleatorio para el refresh token

---

## Endpoints

A continuación se listan los endpoints implementados por los controladores actuales (`AuthController`,
`UsuariosController`, `DiscordController`, `AdminController`). La tabla incluye método HTTP, ruta completa, tipo de
autenticación/rol requerido, DTO de request (si aplica), DTO de response y una descripción corta.

| Método | Ruta                                    | Auth / Rol    | Request                                           | Response                                          | Descripción / Notas                                                                                                                                                                               |
|--------|-----------------------------------------|---------------|---------------------------------------------------|---------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| POST   | `/v1/usuarios/auth/register`            | Public        | `CrearUsuarioRequest`                             | `UsuarioResponse` (201)                           | Registro de nuevo usuario; genera token de verificación.                                                                                                                                          |
| POST   | `/v1/usuarios/auth/verify-email`        | Public        | `VerificarEmailRequest`                           | void (200)                                        | Verifica email usando token de verificación.                                                                                                                                                      |
| POST   | `/v1/usuarios/auth/resend-verification` | Public        | `ReenviarVerificacionRequest`                     | void (200)                                        | Reenvía token de verificación si procede.                                                                                                                                                         |
| POST   | `/v1/usuarios/auth/forgot-password`     | Public        | `SolicitarRestablecimientoRequest`                | void (200)                                        | Inicia flujo de restablecimiento de contraseña (envía token si existe el email).                                                                                                                  |
| POST   | `/v1/usuarios/auth/reset-password`      | Public        | `RestablecerContrasenaRequest`                    | void (200)                                        | Restablece contraseña usando token temporal.                                                                                                                                                      |
| POST   | `/v1/usuarios/auth/login`               | Public        | `LoginRequest`                                    | `AuthResponse` (200)                              | Autentica credenciales; devuelve `accessToken` y `refreshToken`.                                                                                                                                  |
| POST   | `/v1/usuarios/auth/refresh`             | Public        | `RefreshTokenRequest`                             | `AuthResponse` (200)                              | Rota refresh token y emite nuevos tokens.                                                                                                                                                         |
| POST   | `/v1/usuarios/auth/logout`              | Authenticated | `LogoutRequest`                                   | void (204)                                        | Revoca refresh token; logout del usuario autenticado. El controlador recibe `@AuthenticationPrincipal UUID userId` y retorna 204 No Content.                                                      |
| GET    | `/v1/usuarios/auth/me`                  | Authenticated | —                                                 | `UsuarioResponse` (200)                           | Devuelve el perfil del usuario autenticado (userId inyectado por `@AuthenticationPrincipal`).                                                                                                     |
| PUT    | `/v1/usuarios/password`                 | Authenticated | `CambiarContrasenaRequest`                        | void (200)                                        | Cambiar la contraseña del usuario autenticado.                                                                                                                                                    |
| PUT    | `/v1/usuarios/email`                    | Authenticated | `CambiarCorreoRequest`                            | void (200)                                        | Cambiar el email del usuario autenticado (puede requerir nueva verificación).                                                                                                                     |
| PATCH  | `/v1/usuarios`                          | Authenticated | `EditarPerfilUsuarioRequest`                      | `UsuarioResponse` (200)                           | Editar perfil del usuario autenticado (username, avatar, language, etc.).                                                                                                                         |
| GET    | `/v1/usuarios/{id}`                     | Public        | —                                                 | `UsuarioResponse` (200)                           | Obtener datos públicos del usuario por ID (controlador usa `@PathVariable String id`).                                                                                                            |
| PUT    | `/v1/usuarios/discord`                  | Authenticated | `VincularDiscordRequest`                          | `UsuarioResponse` (200)                           | Vincular cuenta de Discord al perfil del usuario autenticado.                                                                                                                                     |
| DELETE | `/v1/usuarios/discord`                  | Authenticated | —                                                 | `UsuarioResponse` (200)                           | Desvincular la cuenta de Discord del usuario autenticado.                                                                                                                                         |
| PATCH  | `/v1/usuarios/admin/{id}/rol`           | ADMIN         | `CambiarRolUsuarioRequest`                        | `UsuarioResponse` (200)                           | Cambiar el rol de un usuario (ADMIN/USER/MODERATOR). Endpoint de administración.                                                                                                                  |
| PATCH  | `/v1/usuarios/admin/{id}/estado`        | ADMIN         | `CambiarEstadoUsuarioRequest`                     | `UsuarioResponse` (200)                           | Cambiar estado de un usuario (SUSPENDER, REACTIVAR, ELIMINAR lógicamente). Endpoint de administración.                                                                                            |
| GET    | `/v1/usuarios/admin/users`              | ADMIN         | query: `username` (optional), `estado` (optional) | `UsuarioResponse` / `List<UsuarioResponse>` (200) | Búsqueda/listado de usuarios. Si `username` está presente devuelve un único `UsuarioResponse`; si `estado` está presente devuelve lista filtrada; sin parámetros devuelve todos (administración). |
| DELETE | `/v1/usuarios/admin/{id}`               | ADMIN         | —                                                 | void (204)                                        | Eliminar usuario.                                                                                                                                                                                 |

> Notas:
> - "Public" indica endpoints que no requieren token (según controladores actuales); el API Gateway puede exponerlos
    como públicos.
> - "Authenticated" indica endpoints que usan `@AuthenticationPrincipal` y requieren que el Gateway haya validado el JWT
    y haya inyectado el principal.
> - Para endpoints administrativos se recomienda `hasRole('ADMIN')` en la configuración de seguridad del servicio o del
    Gateway.
> - Los request/response DTOs mencionados corresponden a los nombres usados en los controladores. Consulta
    `infrastructure/in/api/dto/` para detalles de cada DTO.

---

## Seguridad

### Configuración actual

- **Spring Security** habilitado con configuración permisiva para desarrollo
- **BCryptPasswordEncoder** con strength 10 para hashing de contraseñas
- **JWT Token Generation** con HS256 algorithm
    - Access tokens: 15 minutos de expiración (configurable)
    - Refresh tokens: 7 días de expiración (configurable)
    - Secret key: Configurable vía `jwt.secret` (variable de entorno en producción)
- CORS configurado para permitir todas las origenes (desarrollo)
- CSRF deshabilitado (API stateless)

### Arquitectura de autenticación

**Responsabilidades de usuarios:**

- ✅ Generar access tokens (JWT) y refresh tokens
- ✅ Validar credenciales en `/auth/login`
- ✅ Rotar refresh tokens en `/auth/refresh`
- ✅ Revocar tokens en `/auth/logout`
- ❌ **NO valida** tokens JWT en peticiones entrantes

**Responsabilidades del API Gateway (futuro):**

- ✅ Validar firma de JWT en cada petición
- ✅ Verificar expiración de tokens
- ✅ Extraer claims (userId, roles)
- ✅ Decidir rutas públicas vs protegidas
- ✅ Rechazar tokens inválidos o expirados

> **Separación de responsabilidades**: Este microservicio **emite** tokens pero el Gateway los **valida**.

### Protección de endpoints

**Público (sin autenticación - Gateway permite sin token):**

- `POST /v1/usuarios/auth/register`
- `POST /v1/usuarios/auth/login`
- `POST /v1/usuarios/auth/refresh`
- `POST /v1/usuarios/auth/verify-email`
- `POST /v1/usuarios/auth/resend-verification`
- `POST /v1/usuarios/auth/forgot-password`
- `POST /v1/usuarios/auth/reset-password`

**Autenticado (requiere JWT validado por Gateway):**

- `GET /v1/usuarios/auth/me` → Usuario autenticado
- `POST /v1/usuarios/auth/logout` → Usuario autenticado
- `PUT /v1/usuarios/password` → Usuario autenticado (cambiar propia contraseña)
- `PUT /v1/usuarios/email` → Usuario autenticado (cambiar propio email)
- `PATCH /v1/usuarios` → Usuario autenticado (editar perfil propio)
- `GET /v1/usuarios/{id}` → Cualquier usuario autenticado (obtener datos públicos por id)
- `PUT /v1/usuarios/discord` → Usuario autenticado (vincular Discord)
- `DELETE /v1/usuarios/discord` → Usuario autenticado (desvincular Discord)

**Administración (requerir rol ADMIN en Gateway o en la aplicación):**

- `PATCH /v1/usuarios/admin/{id}/estado` → ADMIN (cambiar estado del usuario)
- `PATCH /v1/usuarios/admin/{id}/rol` → ADMIN (cambiar rol del usuario)
- `GET /v1/usuarios/admin/users` → ADMIN (listar/filtrar usuarios; admite `username` y `estado` como query params)
- `DELETE /v1/usuarios/admin/{id}` → ADMIN (eliminar usuario / soft delete)

> Nota: la tabla de endpoints y la sección anterior se han alineado con las rutas actuales que aparecen en los
> controladores `AuthController`, `UsuariosController`, `DiscordController` y `AdminController`. Si prefieres mantener
> un
> endpoint público de búsqueda `/v1/usuarios/users` (sin prefijo `admin`), indícamelo y lo añado/modifico en los
> controladores y en la documentación.

---

## Testing

### Estrategia de testing

### 1. Tests de dominio (Unit Tests)

**Ubicación:** `src/test/java/.../domain/usuario/`

**Características:**

- Sin dependencias de Spring (`@SpringBootTest` no usado)
- Testing puro de lógica de negocio
- Validación de Value Objects
- Invariantes de agregados

### 2. Tests de aplicación (Unit Tests con Mocks)

**Ubicación:** `src/test/java/.../application/usecases/`

**Características:**

- Uso de Mockito para simular dependencias
- Testing de casos de uso aislados
- Validación de flujos de negocio

### 3. Tests de infraestructura (Integration Tests)

**Ubicación:** `src/test/java/.../infrastructure/`

**Características:**

- `@SpringBootTest` con contexto completo
- Base de datos Postgres para simular entorno real. NUNCA USAR H2
- `@AutoConfigureMockMvc` para tests REST
- Testing de controllers, repositories, mappers

### 4. Tests de integración de flujo completo

**Ubicación:** `src/test/java/.../integration/`

### Ejecución de tests

```bash
# Todos los tests
./mvnw test

# Solo tests de domain
./mvnw test -Dtest="**/domain/**/*Test"

# Solo tests de integración
./mvnw test -Dtest="**/integration/**/*Test"

# Con coverage
./mvnw test jacoco:report
```

---

## Ejecución local

### Prerrequisitos

- **Java 21**
- **Maven 3.9+** (incluido wrapper `./mvnw`)
- **Docker** y **Docker Compose** (para PostgreSQL)
- **Git** para control de versiones

### Con PostgreSQL (producción-like)

```bash
# 1. Levantar PostgreSQL con Docker Compose
cd gameEstado-listo
docker compose up -d postgres

# 2. Verificar que PostgreSQL está corriendo
docker compose ps

# 3. Ejecutar el microservicio
cd usuarios
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# La aplicación se conecta a PostgreSQL en localhost:5432
```

**Configuración PostgreSQL (application.properties):**

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/usuarios_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

### Opción 3: Dockerizar el microservicio

```bash
# Desde la raíz del proyecto
cd gameEstado-listo

# Construir y levantar todos los servicios
docker compose up --build

# Solo microservicio de usuarios + PostgreSQL
docker compose up --build usuarios postgres
```

**Dockerfile incluido:**

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/usuarios-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Hot reload durante desarrollo

```bash
# Instalar Spring Boot DevTools (ya incluido en pom.xml)
# Cualquier cambio en código Java se recarga automáticamente
./mvnw spring-boot:run
```

### Logs y debugging

```bash
# Ver logs en tiempo real
./mvnw spring-boot:run

# Habilitar modo DEBUG
./mvnw spring-boot:run -Dspring-boot.run.arguments=--logging.level.com.gamelisto=DEBUG

# Ejecutar con debugger en puerto 5005
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
```

---

## Variables de entorno

### Configuración de base de datos

| Variable                        | Default                  | Descripción                                        |
|---------------------------------|--------------------------|----------------------------------------------------|
| `SPRING_DATASOURCE_URL`         | `jdbc:h2:mem:usuariosdb` | URL de conexión JDBC                               |
| `SPRING_DATASOURCE_USERNAME`    | `sa`                     | Usuario de BD                                      |
| `SPRING_DATASOURCE_PASSWORD`    | -                        | Contraseña de BD                                   |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update`                 | Gestión de schema (`validate`, `update`, `create`) |

### Configuración de JWT

| Variable                 | Default                                   | Descripción                                             |
|--------------------------|-------------------------------------------|---------------------------------------------------------|
| `JWT_SECRET`             | `default-secret-key-change-in-production` | Clave secreta para firmar JWT (¡CAMBIAR EN PRODUCCIÓN!) |
| `JWT_EXPIRATION`         | `900000`                                  | Expiración de access token en ms (15 min)               |
| `JWT_REFRESH_EXPIRATION` | `604800000`                               | Expiración de refresh token en ms (7 días)              |

> **⚠️ SEGURIDAD**: En producción, `JWT_SECRET` debe ser una cadena aleatoria de al menos 256 bits (32 caracteres). Usar
> variables de entorno, nunca hardcodear.

### Configuración de aplicación

| Variable                 | Default | Descripción                                   |
|--------------------------|---------|-----------------------------------------------|
| `SERVER_PORT`            | `8080`  | Puerto HTTP del microservicio                 |
| `SPRING_PROFILES_ACTIVE` | `dev`   | Perfil activo (`dev`, `prod`, `test`)         |
| `TOKEN_EXPIRATION_HOURS` | `24`    | Expiración de tokens de verificación de email |

### Configuración de email

| Variable               | Default                 | Descripción                      |
|------------------------|-------------------------|----------------------------------|
| `SPRING_MAIL_HOST`     | `smtp.gmail.com`        | Servidor SMTP                    |
| `SPRING_MAIL_PORT`     | `587`                   | Puerto SMTP                      |
| `SPRING_MAIL_USERNAME` | -                       | Usuario del servidor de email    |
| `SPRING_MAIL_PASSWORD` | -                       | Contraseña del servidor de email |
| `MAIL_FROM`            | `noreply@gamelisto.com` | Dirección remitente              |

### Ejemplo de configuración para producción

```bash
# .env file (NO COMMITEAR)
JWT_SECRET=tu-clave-super-secreta-de-al-menos-32-caracteres-aqui
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/usuarios_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your-secure-password
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
```

---

## Convenciones de código

### Nomenclatura

#### Capa de Dominio

- **Entidades**: `Usuario` (sustantivo singular)
- **Value Objects**: `UsuarioId`, `Email`, `Username`
- **Enums**: `EstadoUsuario`, `Rol`, `Idioma`
- **Repositorios (interfaces)**: `RepositorioUsuarios`
- **Excepciones**: `DomainException`

#### Capa de Aplicación

- **Use Cases**: `CrearUsuarioUseCase`, `CrearUsuarioHandle` (Handle interface, UseCase la implementa)
- **Commands/Queries**: `CrearUsuarioCommand`, `EditarPerfilUsuarioCommand`
- **DTOs**: `UsuarioResult`
- **Ports**: `IEmailService`, `INotificationService`
- **Excepciones**: `ApplicationException`

#### Capa de Infraestructura

- **Entidades JPA**: `UsuarioEntity`
- **Repositorios**: `RepositorioUsuariosPostgre`
- **Controllers**: `UsuariosController`
- **Request DTOs**: `CrearUsuarioRequest`, `EditarPerfilRequest`
- **Response DTOs**: `UsuarioResponse`
- **Mappers**: `UsuarioMapper`
- **Excepciones**: `InfaestructureException`

### Reglas de código

1. **Inmutabilidad**: Value Objects son `final` e inmutables
2. **Validación en construcción**: No crear objetos inválidos
3. **No exponer entidades JPA**: Usar mappers
4. **Single Responsibility**: Un use case = una responsabilidad
5. **Dependency Inversion**: Inyectar interfaces, no implementaciones
6. **Tests con nombres descriptivos**: `debe[ComportamientoEsperado]`
7. **Español en nombres de negocio**: `CrearUsuarioUseCase`, `EstadoUsuario`
8. **Sin lógica de negocio en controllers**: Solo conversión Request → Command
9. **Excepciones de dominio**: Nunca `RuntimeException` genérica
10. **DTOs para inter-capa**: Nunca pasar entidades directamente

