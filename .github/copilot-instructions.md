# GameListo – Copilot Instructions

## Project Overview

GameListo is a microservices-based social gaming platform built with Spring Boot 3.5.8 and Java 21, using **Domain-Driven Design (DDD)** and **Hexagonal Architecture**. Users manage game libraries, create custom lists, share experiences, and connect with other players. Data is sourced from IGDB API.

## Architecture Principles

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
- **Anti-Corruption Layer**: Mappers translate between domain entities and infrastructure entities (e.g., `UsuarioMapper` converts `Usuario` ↔ `UsuarioEntity`)

## Key Conventions

### Naming Patterns

**Domain Layer:**
- Entities: `Usuario` (aggregate root)
- Value Objects: `UsuarioId`, `Email`, `Username`, `Avatar`, `PasswordHash`, `DiscordUserId`, `DiscordUsername`, `TokenVerificacion`
- Enums: `EstadoUsuario`, `Rol`, `Idioma`
- Repositories: `RepositorioUsuarios` (interface)
- Exceptions: `EntidadNoEncontrada`, `UsernameYaExisteException`, `EmailYaRegistradoException`, `TokenInvalidoException`

**Application Layer:**
- Use Cases: `CrearUsuarioUseCase`, `EditarPerfilUsuarioUseCase`, `ObtenerUsuarioPorId`, `ObtenerTodosLosUsuariosUseCase`, `EliminarUsuarioUseCase`, `CambiarEstadoUsuarioUseCase`, `CambiarContrasenaUseCase`, `VerificarEmailUseCase`, `ReenviarVerificacionUseCase`, `RestablecerContrasenaUseCase`
- Commands/Queries: `CrearUsuarioCommand`, `EditarPerfilUsuarioCommand`, `CambiarContrasenaCommand`, `CambiarEstadoUsuarioCommand`, `VerificarEmailCommand`, `RestablecerContrasenaCommand`
- DTOs: `UsuarioDTO` (for inter-layer communication)

**Infrastructure Layer:**
- JPA Entities: `UsuarioEntity` (with `@Entity`, `@Table`)
- Mappers: `UsuarioMapper` (converts between domain and JPA entities)
- Repositories: `RepositorioUsuariosPostgre` implements `RepositorioUsuarios`
- Controllers: `UsuariosController` (REST endpoints at `/v1/usuarios`)
- Request/Response DTOs: `CrearUsuarioRequest`, `UsuarioResponse`

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
    
    public String value() { return value; }
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

**usuarios-service** manages user profiles and accounts:
- ✅ Profile CRUD (username, email, avatar, language, notifications)
- ✅ User registration with email verification (token-based, 24h expiration)
- ✅ Password reset flow with token
- ✅ Discord OAuth2 integration (link/unlink)
- ✅ User states: `PENDIENTE_DE_VERIFICACION`, `ACTIVO`, `SUSPENDIDO`, `ELIMINADO`
- ✅ Roles: `USER`, `ADMIN`, `MODERATOR`
- ✅ Multi-language support: `ESP`, `ENG`
- ❌ NOT responsible for: JWT generation/validation, session management (handled by `auth-service`)

**Communication:**
- REST for synchronous queries
- RabbitMQ events for async updates (infrastructure in `/messaging` - not yet implemented)
- GraphQL BFF for frontend data aggregation

## Development Workflow

### Build & Run
```bash
cd usuarios-service
./mvnw clean install           # Build with Maven
./mvnw spring-boot:run         # Run locally (uses H2 in-memory DB)
```

### Database
- **Local/Test**: H2 in-memory (`jdbc:h2:mem:usuariosdb`)
- **Production**: PostgreSQL (configured via `application.properties`)
- Schema managed by Hibernate (`ddl-auto=update`)

### Testing Strategy
- Unit tests for domain (no Spring, no DB)
- Integration tests with `@SpringBootTest` + H2
- REST API tests with MockMvc
- Focus on value object validation and aggregate behavior
- Usa el patrón AAA (Arrange–Act–Assert).
- Usa DisplayName con descripciones en español.
- Nombra los métodos como `debe[ComportamientoEsperado]`.
- Para dominio → tests puros sin Spring.
- Para application → mocks con Mockito.
- Para infrastructure → integración con SpringBootTest y H2.

## Security

- **BCryptPasswordEncoder** for password hashing (strength: 10)
- Spring Security configured in `infrastructure/security/SecurityConfig.java`
- Currently permits all requests (development mode) - JWT integration pending with `auth-service`

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

## References

- Main README: `/README.md` (platform overview)
- Service README: `/usuarios-service/README-usuarios.md`
- Architecture checklists: `/usuarios-service/.github/*.md`
