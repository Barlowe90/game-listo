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

## Principios

KISS - Keep It Simple Stupid.

Esto es un TFG, estoy yo solo haciéndolo, por lo que es MUY IMPORTANTE hacer una versión simple.

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

### Platform Architecture Overview

GameListo sigue una **arquitectura de microservicios** con componentes especializados:

- **API Gateway**: Punto de entrada único (Spring Cloud Gateway)
- **GraphQL BFF**: Backend for Frontend que agrega datos de múltiples microservicios
- **Core Services**: Microservicios de dominio (usuarios, catálogo, biblioteca, etc.)
- **Search Service**: Microservicio especializado de búsqueda con OpenSearch
- **Event Bus**: RabbitMQ para comunicación asíncrona entre servicios

**Data Flow:**

```
Frontend → API Gateway → GraphQL BFF → Microservicios (REST)
                                    ↓
                               Search Service (OpenSearch)
                                    ↑
                            Event Bus (RabbitMQ)
```

### api-gateway (Spring Cloud Gateway)

**API Gateway** - Puerta de entrada única para todos los microservicios:

**Responsibilities:**

- ✅ JWT Token Validation: Verifica firma, expiración y claims de tokens JWT
- ✅ Token Revocation: Verifica blacklist de tokens revocados en Redis (`revoked:jti:<JTI>`)
- ✅ Rate Limiting: Limita 100 peticiones/minuto por IP usando Redis (`rate_limit:<IP>`)
- ✅ CORS Management: Políticas CORS centralizadas para frontend
- ✅ Request Routing: Enruta peticiones a microservicios según el path
- ✅ Header Enrichment: Agrega headers `X-User-*` con información del usuario autenticado
- ✅ Public/Protected Routes: Diferencia rutas públicas (login, registro) de protegidas

**Components:**

- `JwtAuthenticationFilter` (Order: -100): Valida JWT y enriquece headers
- `RateLimitFilter` (Order: -50): Control de tráfico por IP
- `JwtValidator`: Parsea y valida tokens JWT usando JJWT library
- `TokenRevocationService`: Verifica tokens revocados en Redis
- `RedisConfig`: Configuración de Redis reactivo (WebFlux)
- `SecurityConfig`: Desactiva autenticación por defecto de Spring Security

**Routes Configuration:**

```properties
# Public routes (no JWT required)
/v1/usuarios/auth/login
/v1/usuarios/auth/refresh
/v1/usuarios/registro
/v1/usuarios/verificar-email
/actuator/health
# Protected routes (JWT required)
/v1/usuarios/** → usuarios-service:8081
/v1/catalogo/** → catalogo-service:8082 (planned)
```

**Critical Notes:**

- ⚠️ `jwt.secret` MUST be identical in Gateway and usuarios-service
- ⚠️ Gateway uses WebFlux (reactive), NOT Spring MVC
- ⚠️ Redis is required for rate limiting and token revocation
- ⚠️ Microservices trust `X-User-*` headers (internal network only)
- ⚠️ Gateway validates tokens, microservices DO NOT

**Testing:**

```powershell
# Run test script
.\api-gateway\test-gateway.ps1

# Manual tests
curl http://localhost:8090/actuator/health
curl -X POST http://localhost:8090/v1/usuarios/auth/login -H "Content-Type: application/json" -d '{"username":"test","password":"pass"}'
curl http://localhost:8090/v1/usuarios/auth/me -H "Authorization: Bearer <token>"
```

**Documentation:**

- Main README: `api-gateway/README-gateway.md`
- Architecture: `api-gateway/ARQUITECTURA.md`
- Next steps: `api-gateway/GUIA-CONTINUACION.md`

---

### usuarios-service

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

---

### catalogo-service (⏳ In Development)

**catalogo-service** manages the game catalog with IGDB API integration:

**⚠️ IMPORTANT - IGDB Credentials:**

- The developer already has IGDB credentials (Client ID and Access Token)
- Credentials are managed manually in the `.env` file
- NO need to implement OAuth2 flow or token generation
- NO need to create scripts to obtain tokens
- The developer will update the token when it expires

**Responsibilities:**

- ⏳ Game catalog (CRUD operations)
- ⏳ Platform catalog (synchronized from IGDB)
- ⏳ Automated IGDB data ingestion (Spring Scheduler)
- ⏳ Rich game details (screenshots, videos) stored in MongoDB
- ⏳ Game-Platform relationships (PostgreSQL)
- ⏳ Publish events when new games are added/updated (RabbitMQ)

**Data Sources:**

- **IGDB API**: External source for game data
- **PostgreSQL**: Structured data (games, platforms, relationships)
- **MongoDB**: Rich content (screenshots, videos, extended descriptions)

**Communication:**

- REST API for CRUD operations (`/v1/catalogo/*`)
- RabbitMQ events: `GameCreated`, `GameUpdated`, `PlatformSynchronized`
- Consumed by: search-service (for indexing), biblioteca-service (for user libraries)

**Current Status:** FASE 2 completed (domain + application layers), FASE 3 in progress (infrastructure adapters)

---

### search-service (⏳ Planned)

**search-service** provides fast, full-text search and autocomplete for games using OpenSearch:

**Responsibilities:**

- 🔍 Full-text search across game catalog (name, summary, tags)
- 🔍 Autocomplete/type-ahead suggestions for search box
- 🔍 Faceted filtering (genre, platform, year, rating)
- 🔍 Lightweight sorting (relevance, name, release date)
- 🔍 Search group (find players looking for teammates)

**Data Source:**

- **Event-driven indexing**: Listens to RabbitMQ events from catalogo-service
- **Events consumed**: `GameCreated`, `GameUpdated`, `GameDeleted`, `PlatformSynchronized`
- **No direct DB access**: Search index is built from events only

**Technology:**

- **OpenSearch** as search engine
- **Spring Data OpenSearch** for repository pattern
- **Event listeners** via Spring AMQP (RabbitMQ)

**API Endpoints (Planned):**

```
GET /v1/search/games?q={query}&platform={id}&genre={id}&page={n}
GET /v1/search/autocomplete?q={query}&limit={n}
GET /v1/search/groups?game={id}&platform={id}
```

**Communication:**

- Consumes events from: catalogo-service, biblioteca-service (for group search)
- Exposes REST API consumed by: GraphQL BFF
- Cache layer: Redis for frequently accessed queries

**Why OpenSearch?**

- Optimized for search queries (faster than PostgreSQL LIKE queries)
- Faceted filtering and aggregations
- Autocomplete with ngram tokenizers
- Horizontal scalability for large catalogs

---

### graphql-bff (⏳ Planned)

**graphql-bff** (Backend for Frontend) aggregates data from multiple microservices into a single GraphQL API:

**Responsibilities:**

- 📊 Data aggregation from multiple services in a single query
- 📊 Reduce round-trips between frontend and backend
- 📊 Type-safe schema with GraphQL SDL
- 📊 Resolver composition (fetch from REST APIs of microservices)
- 📊 Client-specific data shaping (avoid over-fetching/under-fetching)

**Technology:**

- **Spring GraphQL** (Spring Boot integration)
- **GraphQL Schema** with federation patterns
- **REST clients** (WebClient) to call microservices
- **DataLoader** for batching and caching

**Example Queries:**

```graphql
# Single query to get user profile + game library + friend activity
query UserDashboard($userId: ID!) {
  user(id: $userId) {
    username
    avatar
    library {
      game {
        name
        cover
        platforms
      }
      status
      rating
    }
    friends {
      username
      currentlyPlaying {
        name
        cover
      }
    }
  }
}

# Search games with autocomplete
query SearchGames($query: String!, $platforms: [ID!]) {
  searchGames(query: $query, platforms: $platforms) {
    id
    name
    cover
    summary
    platforms {
      name
      abbreviation
    }
  }
}
```

**Services Called:**

- `usuarios-service`: User profile, friends
- `catalogo-service`: Game details, platforms
- `biblioteca-service`: User library, ratings
- `search-service`: Game search, autocomplete
- `social-service`: Friend graph, recommendations

**Why GraphQL BFF?**

- Frontend can request exactly what it needs in one query
- Reduces chattiness (no multiple REST calls)
- Type safety with TypeScript codegen
- Better DX for frontend developers

**Communication:**

- Exposes GraphQL API at `/graphql` (behind API Gateway)
- Calls microservices via REST (internal network)
- Uses DataLoader for N+1 query prevention
- Cache layer: Redis for resolver results

---

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
