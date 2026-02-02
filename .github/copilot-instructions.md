# GameListo – Copilot Instructions

## Project Overview

GameListo is a microservices-based social gaming platform built with Spring Boot 3.5.8 and Java 21, using *
*Domain-Driven Design (DDD)** and **Hexagonal Architecture**. Users manage game libraries, create custom lists, share
experiences, and connect with other players. Data is sourced from IGDB API.

La solución sigue una arquitectura de microservicios, donde cada dominio - usuarios, catálogo, biblioteca,
publicaciones, notificaciones, búsqueda y social- se implementa como un servicio independiente. Estos servicios
colaboran entre sí mediante APIs REST y mensajería asíncrona. Para construir estos servicios se emplea Spring Boot.

La capa perimetral se resuelve con Spring Cloud - en concreto Spring Cloud Gateway- que actúa como puerta
de entrada única. Este componente gestiona el enrutamiento, aplica las políticas de CORS, realiza la autenticación
en el borde (en el propio gateway) y aplica mecanismos de rate-limiting con apoyo en Redis.

En la exposición hacia el cliente web se utiliza un Backend for Frontend implementado con Spring GraphQL. GraphQL
permite componer en una única consulta información procedente de varios microservicios, reduciendo round-trips. Los
microservicios internos exponen REST, mientras que GraphQL se limita al BFF como fachada tras el gateway.

Para la persistencia de datos se adopta una estrategia políglota eligiendo el amacén según el tipo de información.

- PostgreSQL como base relacional principal para datos estructurados y transaccionales: usuarios,
  biblioteca/estados/valoraciones, relaciones juego-plataforma y fechas de lanzamiento canónicas. PostgreSQL destaca
  por un esquema y validaciones fuertes y un optimizador de consultas
  maduro.
- MongoDB para contenido flexible y de alto volumen (detalles enriquecidos de videojuegos provenientes de IGDB y
  publicaciones). MongoDB aporta un esquema flexible y buen rendimiento en lecturas/escrituras.
- Neo4j como base de grafos para el grafo social (amistades, “amigos que juegan X”, afinidad). Neo4j es muy
  eficiente en recorridos y consultas de relaciones (amigos de amigos y recomendaciones).
- OpenSearch como motor de búsqueda y filtrado facetado (juegos y buscar grupo), incluyendo autocompletado y
  ordenación ligera cuando sea necesario. Proporciona búsqueda por texto completo, óptimo para consultas rápidas.

La comunicación asíncrona entre servicios se implementa con RabbitMQ (Spring AMQP); este bus transporta los
eventos de dominio. La ingesta de datos externos (API de IGDB) se automatiza con Spring Scheduler: se realizan
cargas incrementales, se normalizan los datos a los modelos internos y se publican eventos para reindexar OpenSearch e
invalidar cachés.

La información de fechas de lanzamiento procede de la API de IGDB. En cada ciclo de ingesta, el sistema normaliza
estas fechas al modelo interno y calcula una fecha de lanzamiento canónica por juego (almacenada en UTC). Con esta
base, la lista de próximos lanzamientos se construye con una consulta que filtra juegos cuya fecha canónica es
posterior a la fecha actual.

En materia de seguridad, se usa Spring Security con JWT. Las sesiones son stateless (el servidor no conserva estado de
sesión entre peticiones; cada solicitud se autentica por sí misma con el
token). Para revocación/rotación de tokens se emplea Redis como lista de identificadores jti y refresh tokens
revocados, con TTL lo que permite logout inmediato y evita su reutilización.

Además, se aplican políticas de CSP desde la capa de entrega del frontend, y se asegura el almacenamiento de secretos.
La mensajería en tiempo real (mensajes directos y chats de grupo) se realiza mediante un botón que llama a la
aplicación de Discord, previamente añadiendo el usuario e id a la cuenta del mismo.

## Architecture Principles!

### Hexagonal Architecture (Ports & Adapters)

```
/domain          → Pure business logic, zero external dependencies
/application     → Use cases, coordinates domain logic, defines ports
/infrastructure  → Adapters for REST, persistence, messaging, security
```

**Dependency Rule**: `infrastructure` → `application` → `domain` (never reverse).

### Domain-Driven Design

- **Value Objects**: All domain primitives are immutable VOs with validation (e.g., `Email`, `Username`, `UsuarioId`)
- **Aggregates**: Entities like `Usuario` control their lifecycle with factory methods (`create()`, `reconstitute()`)
- **Repository Ports**: Interfaces in `/domain/repositories`, implementations in `/infrastructure/persistence`
- **Anti-Corruption Layer**: Mappers translate between domain entities and infrastructure entities (e.g.,
  `UsuarioMapper` converts `Usuario` ↔ `UsuarioEntity`)

## Key Conventions

### Naming Patterns

**Domain Layer:**

- Entities: `Usuario` (aggregate root), `RefreshToken` (aggregate for token management)
- Value Objects: `UsuarioId`, `Email`, `Username`, `Avatar`, `PasswordHash`, `DiscordUserId`, `DiscordUsername`,
  `TokenVerificacion`, `RefreshTokenId`, `TokenValue`
- Enums: `EstadoUsuario`, `Rol`, `Idioma`
- Repositories: `RepositorioUsuarios` (interface), `RepositorioRefreshTokens` (interface)
- Exceptions: `EntidadNoEncontrada`, `UsernameYaExisteException`, `EmailYaRegistradoException`,
  `TokenInvalidoException`, `CredencialesInvalidasException`, `RefreshTokenExpiradoException`

**Application Layer:**

- Use Cases: `CrearUsuarioUseCase`, `EditarPerfilUsuarioUseCase`, `ObtenerUsuarioPorId`,
  `ObtenerTodosLosUsuariosUseCase`, `EliminarUsuarioUseCase`, `CambiarEstadoUsuarioUseCase`, `CambiarContrasenaUseCase`,
  `VerificarEmailUseCase`, `ReenviarVerificacionUseCase`, `RestablecerContrasenaUseCase`, `LoginUseCase`,
  `RefreshTokenUseCase`, `LogoutUseCase`, `ObtenerPerfilAutenticadoUseCase`
- Commands/Queries: `CrearUsuarioCommand`, `EditarPerfilUsuarioCommand`, `CambiarContrasenaCommand`,
  `CambiarEstadoUsuarioCommand`, `VerificarEmailCommand`, `RestablecerContrasenaCommand`, `LoginCommand`,
  `RefreshTokenCommand`, `LogoutCommand`
- DTOs: `UsuarioDTO`, `AuthResponseDTO` (contains access/refresh tokens), `TokenDTO`

**Infrastructure Layer:**

- JPA Entities: `UsuarioEntity`, `RefreshTokenEntity` (for token persistence)
- Mappers: `UsuarioMapper` (converts between domain and JPA entities)
- Repositories: `RepositorioUsuariosPostgre` implements `RepositorioUsuarios`, `RefreshTokenRepository` (JPA repository)
- Controllers: `UsuariosController` (REST endpoints at `/v1/usuarios`), `AuthController` (auth endpoints at
  `/v1/usuarios/auth`)
- Request/Response DTOs: `CrearUsuarioRequest`, `UsuarioResponse`, `LoginRequest`, `RefreshTokenRequest`, `AuthResponse`
- Auth Utilities: `JwtUtils` (token generation/parsing), `JwtProperties` (configuration properties)

### Code Patterns

**1. Value Objects (Immutable with Validation)**

```java
public final class Email {
    private final String value;

    private Email(String value) {
        // Validation logic here
        this.value = normalize(value);
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String value() {
        return value;
    }
}
```

**2. Domain Entities (Private Constructor + Factory Methods)**

```java
public class Usuario {
    private final UsuarioId id;
    private Username username;

    private Usuario(...) { /* validate invariants */ }

    public static Usuario create(...) { /* for new instances */ }

    public static Usuario reconstitute(...) { /* from persistence */ }

    public void changeUsername(Username newUsername) {
        // Business logic + validation
        this.username = newUsername;
        this.updatedAt = Instant.now();
    }
}
```

**3. Use Cases (Single Responsibility)**

```java

@Service
public class CrearUsuarioUseCase {
    private final RepositorioUsuarios repositorioUsuarios;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioDTO execute(CrearUsuarioCommand command) {
        // 1. Validate business rules
        // 2. Create domain entity
        // 3. Save via repository port
        // 4. Return DTO
    }
}
```

**4. REST Controllers (Thin Layer)**

```java

@RestController
@RequestMapping("/v1/usuarios")
public class UsuariosController {
    // Inject use cases
    // Use @Valid for request validation
    // Convert Request → Command → UseCase → DTO → Response
}
```

**5. Repository Pattern**

- Interface in `domain/repositories` with domain types
- Implementation in `infrastructure/persistence/postgres/repository`
- Use mapper to convert `Usuario` ↔ `UsuarioEntity`
- Return domain types, never expose JPA entities

## Microservice Context

**usuarios-service** manages user profiles, authentication and token generation:

**Profile & Account Management:**

- ✅ Profile CRUD (username, email, avatar, language, notifications)
- ✅ User registration with email verification (token-based, 24h expiration)
- ✅ Password reset flow with token
- ✅ Discord OAuth2 integration (link/unlink)
- ✅ User states: `PENDIENTE_DE_VERIFICACION`, `ACTIVO`, `SUSPENDIDO`, `ELIMINADO`
- ✅ Roles: `USER`, `ADMIN`, `MODERATOR`
- ✅ Multi-language support: `ESP`, `ENG`

**Authentication & JWT Token Generation:**

- ✅ `POST /v1/usuarios/auth/login` → Validates credentials, generates access token (JWT) + refresh token
- ✅ `POST /v1/usuarios/auth/refresh` → Rotates refresh token, generates new access token
- ✅ `POST /v1/usuarios/auth/logout` → Revokes refresh token
- ✅ `GET /v1/usuarios/auth/me` → Returns authenticated user profile from token
- ✅ JWT token generation with configurable secret and expiration
- ✅ Refresh token management (stored in database, can be revoked)
- ❌ NOT responsible for: JWT **validation** in requests (handled by API Gateway - Spring Cloud Gateway)

**Architecture Notes:**

- **Token Generation**: Done by `usuarios-service` (this service)
- **Token Validation**: Done by API Gateway (verifies signature, expiration, claims, routes public/protected endpoints)
- **Why this split?**: Centralized validation at gateway level, while auth logic stays with user domain

**Communication:**

- REST for synchronous queries
- RabbitMQ events for async updates (infrastructure in `/messaging` - not yet implemented)
- GraphQL BFF for frontend data aggregation

## Development Workflow

### Build & Run

**Maven commands (from `usuarios-service/` directory):**

```bash
./mvnw clean install           # Build with Maven
./mvnw spring-boot:run         # Run locally (uses H2 in-memory DB)
./mvnw test                    # Run all tests
./mvnw test -Dtest=ClassName   # Run specific test class
```

**Windows users:** Use `mvnw.cmd` instead of `./mvnw`

### Database

- **Local/Test**: H2 in-memory (`jdbc:h2:mem:usuariosdb`)
- **Production**: PostgreSQL (configured via `application.properties`)
- Schema managed by Hibernate (`ddl-auto=update`)

### Testing Strategy

- **Unit tests for domain**: No Spring, no DB - pure Java tests focused on Value Objects and domain logic
- **Integration tests with `@SpringBootTest` + H2**: Tests for application layer use cases with mocked repositories
- **REST API tests with MockMvc**: Infrastructure layer controller tests
- **Focus on value object validation and aggregate behavior**
- **Test naming conventions**:
    - Use AAA pattern (Arrange–Act–Assert)
    - Use `@DisplayName` with Spanish descriptions
    - Name methods as `debe[ComportamientoEsperado]`
- **Layer-specific testing**:
    - Domain → Pure tests without Spring
    - Application → Mocks with Mockito
    - Infrastructure → Integration with `@SpringBootTest` and H2
- **Test resources**: See `README-TESTS*.md` files in test directories for detailed patterns

## Security

### Password Management

- **BCryptPasswordEncoder** for password hashing (strength: 10)
- Passwords never stored in plain text
- Password validation on login

### JWT Token Management

- **Access Tokens**: Short-lived JWT tokens (configurable expiration, e.g., 15 minutes)
    - Contains: `sub` (userId), `username`, `email`, `roles`, `iat`, `exp`
    - Signed with `HS256` algorithm using secret key from configuration
- **Refresh Tokens**: Long-lived tokens (e.g., 7 days) stored in database
    - UUID-based, can be revoked on logout or security concerns
    - Used to generate new access tokens without re-authentication
- **Token Generation**: Handled by `JwtUtils` in `/infrastructure/auth`
- **Token Validation**: Delegated to **API Gateway** (Spring Cloud Gateway)
    - Gateway validates signature, expiration, claims
    - Gateway routes public vs protected endpoints
    - usuarios-service does NOT validate tokens in incoming requests

### Spring Security Configuration

- Located in `infrastructure/security/SecurityConfig.java`
- In development mode: Permits all requests (no authentication required)
- **Important**: This service generates tokens but does NOT validate them
- Token validation will be added to API Gateway in the future

### Configuration Properties

```properties
# JWT Configuration (application.properties)
jwt.secret=${JWT_SECRET:your-secret-key-change-in-production}
jwt.expiration=900000         # 15 minutes in milliseconds
jwt.refresh-expiration=604800000  # 7 days in milliseconds
```

## Common Tasks

**Add a new Value Object:**

1. Create in `/domain/usuario` with validation logic
2. Use private constructor + static `of()` factory
3. Make it final and immutable
4. Add to `Usuario` entity and `UsuarioEntity` mapping

**Add a new Use Case:**

1. Create `@Service` in `/application/usecases`
2. Define Command/Query DTO in `/application/dto`
3. Inject repository port(s)
4. Use `@Transactional` if modifying data
5. Return application DTO, never domain entities

**Add a new REST Endpoint:**

1. Add method to `UsuariosController`
2. Create Request DTO in `/infrastructure/api/dto` with `@Valid` annotations
3. Create Response DTO or reuse existing
4. Convert: Request → Command → UseCase → DTO → Response
5. Use proper HTTP status codes and `ResponseEntity`

## Anti-Patterns to Avoid

- ❌ Don't expose `UsuarioEntity` (JPA) outside infrastructure layer
- ❌ Don't put business logic in controllers or mappers
- ❌ Don't make domain entities depend on Spring annotations
- ❌ Don't bypass value object validation with setters
- ❌ Don't return `Optional` from use cases (throw exceptions instead)
- ❌ Don't create circular dependencies between layers

## Package Structure & File Organization

**Root Layout:**

```
usuarios-service/
├── src/main/java/com/gamelisto/usuarios_service/
│   ├── domain/          # Pure business logic (no Spring)
│   │   ├── usuario/     # Aggregate root + VOs
│   │   ├── refreshtoken/ # RefreshToken aggregate + VOs
│   │   ├── events/      # Domain events
│   │   ├── exceptions/  # Domain exceptions
│   │   └── repositories/ # Repository interfaces
│   ├── application/     # Use cases + DTOs
│   │   ├── usecases/    # Each use case = 1 file
│   │   ├── dto/         # Commands + inter-layer DTOs
│   │   └── ports/       # External service interfaces
│   └── infrastructure/  # Adapters (Spring-aware)
│       ├── api/rest/    # REST controllers (UsuariosController, AuthController)
│       ├── api/dto/     # Request/Response DTOs
│       ├── auth/        # JWT utilities (JwtUtils, JwtProperties)
│       ├── persistence/postgres/
│       │   ├── entity/  # JPA entities (UsuarioEntity, RefreshTokenEntity)
│       │   ├── mapper/  # Domain ↔ JPA conversion
│       │   └── repository/ # JPA repository implementations
│       ├── messaging/   # RabbitMQ (configured, not yet active)
│       ├── email/       # Email service adapter
│       └── security/    # Spring Security config
└── src/test/java/       # Mirror structure with README-TESTS*.md guides
```

**Port Naming**: `I` prefix for ports (e.g., `IEmailService`, `IUsuarioPublisher`)

## Database Configuration

**Profile-based switching:**

- Local development: PostgreSQL on `localhost:5432/usuarios_db` (user: `guest`)
- Docker Compose: PostgreSQL on `postgres:5432/usuarios_db`
- Tests: H2 in-memory (`jdbc:h2:mem:usuariosdb`) via `application-test.properties`
- Schema: Auto-generated by Hibernate (`spring.jpa.hibernate.ddl-auto=update`)

**Run with Docker:**

```bash
docker-compose up -d          # Starts PostgreSQL + RabbitMQ + service on port 8081
docker-compose logs -f usuarios-service
```

## Event-Driven Architecture (Planned)

- **RabbitMQ** configured in `/infrastructure/messaging/config`
- Events defined in `/domain/events` (e.g., `UsuarioCreado`)
- Publishers in `/infrastructure/messaging/publishers`
- Listeners in `/infrastructure/messaging/listeners`
- Currently: Events logged but not published (integration pending)

## References

- Main README: [README.md](../README.md) (platform overview)
- Service README: [usuarios-service/README-usuarios.md](../usuarios-service/README-usuarios.md)
- Testing Guide: [.github/testing-guide.md](testing-guide.md) (comprehensive test patterns)
- Architecture checklists: `usuarios-service/.github/*.md`
