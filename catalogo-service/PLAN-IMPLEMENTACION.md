# Plan de Implementación - Microservicio Catálogo

> **Objetivo:** Implementar el microservicio de catálogo para GameListo siguiendo arquitectura hexagonal y DDD, con
> ingesta automatizada de IGDB, persistencia dual (PostgreSQL + MongoDB), eventos RabbitMQ y tests con Testcontainers.

---

## 📋 Resumen Ejecutivo

### Stack Tecnológico

- **Java 21** + **Spring Boot 4.0.2**
- **Arquitectura:** Hexagonal (Ports & Adapters) + DDD
- **Persistencia:**
    - PostgreSQL → Datos estructurados (Game, catálogos, relaciones)
    - MongoDB → Contenido enriquecido (screenshots, videos)
- **Mensajería:** RabbitMQ (eventos de integración)
- **API Externa:** IGDB (ingesta de juegos)
- **Testing:** Testcontainers (NO H2)

### Puerto del Servicio

- **8082** (configurado en `application.properties`)

### Modelo de Datos (MVP)

**Versión simplificada inicial** - Los datos obtenidos de IGDB se limitan a:

#### PostgreSQL (datos estructurados):

- **Game**: id, name, summary, cover (URL)
- **Platform**: id, name, abbreviation (sincronizadas desde IGDB)
- Relación M:N: Game ↔ Platform

#### MongoDB (contenido multimedia):

- **GameDetail**: gameId, screenshots[], videos[]

### ⚠️ IMPORTANTE: Sincronización de Catálogos desde IGDB

**TODAS las entidades de catálogo serán obtenidas dinámicamente desde la API de IGDB**, no se mantendrán manualmente
como enums:

- ✅ **Platform** → Endpoint `/v4/platforms` de IGDB
- ✅ **Language** → Endpoint `/v4/languages` de IGDB (futuro)
- ✅ **Genre** → Endpoint `/v4/genres` de IGDB (futuro)
- ✅ **Company** (Developers/Publishers) → Endpoint `/v4/companies` de IGDB (futuro)
- ✅ **GameMode** → Endpoint `/v4/game_modes` de IGDB (futuro)
- ✅ **Theme** → Endpoint `/v4/themes` de IGDB (futuro)
- ✅ **AgeRating** → Endpoint `/v4/age_ratings` de IGDB (futuro)
- ✅ **MultiplayerMode** → Endpoint `/v4/multiplayer_modes` de IGDB (futuro)

**Beneficios de esta estrategia:**

- 🔄 Sincronización automática con IGDB (sin mantenimiento manual)
- 🆔 IDs consistentes con IGDB (facilita relaciones)
- 📊 Información completa (logos, descripciones, slugs, etc.)
- 🚀 Extensible a futuro sin cambios de código

**Campos excluidos del MVP inicial** (se añadirán en futuras iteraciones):

- ⏳ Language (idiomas disponibles)
- ⏳ MultiplayerMode (modos multijugador)
- ⏳ GameTimeToBeat (tiempos de juego)
- ⏳ ReleaseDate (fechas de lanzamiento)
- ⏳ Genres (géneros)
- ⏳ Developers/Publishers (compañías)
- ⏳ AgeRating (clasificación por edades)
- ⏳ GameMode (modos de juego: single-player, multiplayer, etc.)
- ⏳ Theme (temas: acción, horror, fantasía, etc.)

---

## 🏗️ Estructura de Paquetes Completa

```
catalogo-service/src/main/java/com/gamelist/catalogo_service/
├── domain/                           # Capa de dominio (lógica de negocio pura)
│   ├── game/                         # Agregado Game
│   │   ├── Game.java                 # Aggregate root
│   │   ├── GameId.java               # Value Object (Long wrapeado)
│   │   ├── GameName.java             # Value Object (validación: no vacío, max 200)
│   │   ├── Summary.java              # Value Object (max 1000 caracteres)
│   │   └── CoverUrl.java             # Value Object (URL válida de IGDB)
│   ├── catalog/                      # Catálogos (entidades sincronizadas desde IGDB)
│   │   ├── Platform.java             # Entidad catálogo (sincronizada desde IGDB)
│   │   ├── PlatformId.java           # Value Object (Long - ID de IGDB)
│   │   ├── PlatformName.java         # Value Object (nombre completo, ej: "PlayStation 5")
│   │   ├── PlatformAbbreviation.java # Value Object (abreviación, ej: "PS5")
│   │   # FUTURO: Language, Genre, Company, GameMode, Theme, AgeRating (misma estructura)
│   ├── gamedetail/                   # Agregado GameDetail (documental)
│   │   ├── GameDetail.java           # Aggregate root (para Mongo)
│   │   ├── Screenshot.java           # Value Object
│   │   └── Video.java                # Value Object
│   ├── syncstate/                    # Gestión de sincronización
│   │   ├── SyncState.java            # Entidad (key-value checkpoint)
│   │   └── SyncKey.java              # Enum (LAST_SYNCED_GAME_ID)
│   ├── events/                       # Eventos de dominio
│   │   ├── CatalogGameUpserted.java  # Record (gameId, occurredAt, source)
│   │   ├── CatalogSyncBatchCompleted.java # Record (batchSize, lastId, occurredAt)
│   │   ├── CatalogSyncCompleted.java # Record (totalGames, occurredAt)
│   │   └── PlatformsSyncCompleted.java # Record (totalPlatforms, occurredAt)
│   │   # FUTURO: LanguagesSyncCompleted, GenresSyncCompleted, etc.
│   ├── exceptions/                   # Excepciones de dominio
│   │   ├── GameNotFoundException.java
│   │   ├── InvalidGameDataException.java
│   │   └── SyncStateNotFoundException.java
│   └── repositories/                 # Ports de repositorios (interfaces)
│       ├── IGameRepository.java      # findById, save, findByName (paginado)
│       ├── IGameDetailRepository.java # findByGameId, save
│       ├── IPlatformRepository.java  # findById, save, findAll (catálogo)
│       └── ISyncStateRepository.java # findByKey, save
│       # FUTURO: ILanguageRepository, IGenreRepository, ICompanyRepository, etc.
│
├── application/                      # Capa de aplicación (casos de uso)
│   ├── usecases/
│   │   ├── SyncIgdbGamesUseCase.java      # Sincronización batch de juegos IGDB
│   │   ├── SyncPlatformsFromIgdbUseCase.java # Sincronización de plataformas IGDB
│   │   ├── GetGameDetailUseCase.java # Obtener Game + GameDetail (screenshots/videos)
│   │   └── SearchGamesByNameUseCase.java # Búsqueda paginada
│   │   # FUTURO: SyncLanguagesFromIgdbUseCase, SyncGenresFromIgdbUseCase, etc.
│   ├── dto/                          # DTOs inter-capa
│   │   ├── commands/
│   │   │   └── SyncIgdbCommand.java  # (fromId?, limit?)
│   │   ├── queries/
│   │   │   ├── GetGameDetailQuery.java # (gameId)
│   │   │   └── SearchGamesQuery.java # (name, page, size)
│   │   └── results/
│   │       ├── GameDTO.java          # DTO dominio → aplicación
│   │       ├── GameDetailDTO.java
│   │       ├── PlatformDTO.java      # DTO para plataformas
│   │       ├── IgdbGameDTO.java      # DTO desde IGDB (juegos)
│   │       ├── IgdbPlatformDTO.java  # DTO desde IGDB (plataformas)
│   │       └── SyncResultDTO.java
│   │       # FUTURO: IgdbLanguageDTO, IgdbGenreDTO, etc.
│   └── ports/                        # Ports externos (interfaces)
│       ├── IIgdbClientPort.java      # fetchGamesBatch(afterId, limit), fetchPlatforms()
│       │                             # FUTURO: fetchLanguages(), fetchGenres(), fetchCompanies()...
│       └── IEventPublisherPort.java  # publish(DomainEvent)
│
└── infrastructure/                   # Capa de infraestructura (adapters)
    ├── api/
    │   ├── rest/
    │   │   └── CatalogoController.java # REST endpoints
    │   └── dto/
    │       ├── request/
    │       │   └── SyncIgdbRequest.java # (fromId?, limit?)
    │       └── response/
    │           ├── GameDetailResponse.java
    │           ├── GameSearchResponse.java
    │           └── SyncStatusResponse.java
    ├── persistence/
    │   ├── postgres/
    │   │   ├── entity/
    │   │   │   ├── GameEntity.java           # JPA @Entity
    │   │   │   ├── PlatformEntity.java       # JPA @Entity (catálogo sincronizado)
    │   │   │   ├── GamePlatformEntity.java   # JPA @Entity (tabla puente M:N)
    │   │   │   └── SyncStateEntity.java      # JPA @Entity (key-value)
    │   │   │   # FUTURO: LanguageEntity, GenreEntity, CompanyEntity, etc.
    │   │   ├── mapper/
    │   │   │   ├── GameMapper.java           # Domain ↔ JPA Entity
    │   │   │   └── PlatformMapper.java       # Domain ↔ JPA Entity
    │   │   │   # FUTURO: LanguageMapper, GenreMapper, CompanyMapper, etc.
    │   │   └── repository/
    │   │       ├── GameJpaRepository.java    # extends JpaRepository
    │   │       ├── PlatformJpaRepository.java # extends JpaRepository
    │   │       ├── SyncStateJpaRepository.java
    │   │       ├── GameRepositoryPostgres.java # implements IGameRepository
    │   │       ├── PlatformRepositoryPostgres.java # implements IPlatformRepository
    │   │       └── SyncStateRepositoryPostgres.java # implements ISyncStateRepository
    │   │       # FUTURO: LanguageJpaRepository, GenreJpaRepository, etc.
    │   └── mongodb/
    │       ├── document/
    │       │   ├── GameDetailDocument.java   # @Document
    │       │   ├── ScreenshotDocument.java   # Embedded
    │       │   └── VideoDocument.java        # Embedded
    │       ├── mapper/
    │       │   └── GameDetailMapper.java     # Domain ↔ Mongo Document
    │       └── repository/
    │           ├── GameDetailMongoRepository.java # extends MongoRepository
    │           └── GameDetailRepositoryMongo.java # implements IGameDetailRepository
    ├── igdb/
    │   ├── IgdbHttpAdapter.java              # implements IIgdbClientPort
    │   ├── IgdbQueryBuilder.java             # Genera queries DSL de IGDB
    │   ├── IgdbRateLimitHandler.java         # Manejo de 429 (exponential backoff)
    │   └── dto/
    │       ├── IgdbGameResponseDto.java      # Respuesta cruda de IGDB (/games)
    │       └── IgdbPlatformResponseDto.java  # Respuesta cruda de IGDB (/platforms)
    │       # FUTURO: IgdbLanguageResponseDto, IgdbGenreResponseDto, etc.
    ├── messaging/
    │   ├── config/
    │   │   └── MessagingConfig.java          # RabbitMQ exchanges/queues
    │   └── publishers/
    │       └── EventPublisherRabbitMQ.java   # implements IEventPublisherPort
    ├── scheduler/
    │   └── ScheduledSyncJob.java             # @Scheduled para sync incremental
    ├── security/
    │   └── SecurityConfig.java               # Validación headers X-User-* del Gateway
    └── config/
        ├── IgdbProperties.java               # @ConfigurationProperties("igdb")
        ├── PostgresConfig.java               # Configuración JPA
        ├── MongoConfig.java                  # Configuración MongoDB
        └── WebClientConfig.java              # Bean WebClient para IGDB
```

---

## 🎯 Orden de Implementación Detallado

### **FASE 1: Capa de Dominio** (Fundamentos DDD)

#### 1.1 Value Objects básicos

```java
// Orden de creación:
1.GameId.java           // Wrapper de Long con validación > 0
2.GameName.java         // String no vacío, max 200 caracteres
3.Summary.java          // String max 1000 caracteres (puede ser null)
4.CoverUrl.java         // String URL válida (formato IGDB)
```

**Patrón a seguir:**

```java
public final class GameName {
    private final String value;

    private GameName(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidGameDataException("El nombre del juego no puede estar vacío");
        }
        if (value.length() > 200) {
            throw new InvalidGameDataException("El nombre excede los 200 caracteres");
        }
        this.value = value.trim();
    }

    public static GameName of(String value) {
        return new GameName(value);
    }

    public String value() {
        return value;
    }

    // equals, hashCode, toString
}
```

#### 1.2 Value Objects de Platform (catálogo IGDB)

```java
// Crear value objects para Platform:
1.PlatformId.java         // Wrapper de Long (ID de IGDB)
2.PlatformName.java       // String no vacío, max 200 (ej: "PlayStation 5")
3.PlatformAbbreviation.java // String max 50 (ej: "PS5")
```

#### 1.3 Enums de sistema

```java
// Crear enums:
1.SyncKey.java          // LAST_SYNCED_GAME_ID, LAST_SYNCED_PLATFORM_ID, LAST_SYNC_TIMESTAMP
```

#### 1.4 Entidades de catálogo (sincronizadas desde IGDB)

```java
// Platform.java - Entidad catálogo
public class Platform {
    private final PlatformId id;              // ID de IGDB
    private PlatformName name;                 // "PlayStation 5"
    private PlatformAbbreviation abbreviation; // "PS5"
    private String logoUrl;                    // URL del logo (opcional)
    private Integer category;                  // Categoría de IGDB (1=console, 6=pc, etc.)
    private Instant createdAt;
    private Instant updatedAt;

    private Platform(...) { /* Constructor privado */ }

    // Factory para creación desde IGDB
    public static Platform create(PlatformId id, PlatformName name,
                                  PlatformAbbreviation abbreviation) {
        // Validación y asignación
    }

    // Factory para reconstitución desde BD
    public static Platform reconstitute(...) {
    }

    // Métodos de negocio
    public void updateMetadata(PlatformName name, PlatformAbbreviation abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.updatedAt = Instant.now();
    }
}

// FUTURO: Language, Genre, Company, GameMode, Theme, AgeRating (misma estructura)
```

#### 1.5 Agregado Game (Aggregate Root)

```java
// Game.java - Patrón factory:
public class Game {
    private final GameId id;
    private GameName name;
    private Summary summary;
    private CoverUrl coverUrl;
    private Set<Platform> platforms;
    private Instant createdAt;
    private Instant updatedAt;

    private Game(...) { /* Constructor privado */ }

    // Factory para creación nueva (desde IGDB)
    public static Game create(GameId id, GameName name, Summary summary, CoverUrl coverUrl) {
        // Validación de invariantes
        // Inicializar set de platforms vacío
        // createdAt = Instant.now()
    }

    // Factory para reconstitución (desde BD)
    public static Game reconstitute(...) {
    }

    // Métodos de negocio
    public void updateMetadata(GameName name, Summary summary, CoverUrl coverUrl) {
    }

    public void addPlatform(Platform platform) {
    }

    public void setPlatforms(Set<Platform> platforms) {
    }
    // etc.
}
```

#### 1.6 Agregado GameDetail (documental)

```java
// GameDetail.java - Para MongoDB
public class GameDetail {
    private final GameId gameId;
    private List<Screenshot> screenshots;
    private List<Video> videos;

    // Value Objects:
    public record Screenshot(String url, int width, int height) {
    }

    public record Video(String url, String videoId) {
    }
}
```

#### 1.7 Entidad SyncState

```java
// SyncState.java - Checkpoint de sincronización
-key:

SyncKey(enum)
-value:String
-updatedAt:Instant
```

#### 1.8 Eventos de dominio

```java
// Records inmutables:
public record CatalogGameUpserted(
                Long gameId,
                Instant occurredAt,
                String source // "IGDB"
        ) {
    public static CatalogGameUpserted of(Long gameId) {
        return new CatalogGameUpserted(gameId, Instant.now(), "IGDB");
    }
}

public record CatalogSyncBatchCompleted(
        int batchSize,
        Long lastGameId,
        Instant occurredAt
) {
}

public record PlatformsSyncCompleted(
        int totalPlatforms,
        Instant occurredAt
) {
    public static PlatformsSyncCompleted of(int total) {
        return new PlatformsSyncCompleted(total, Instant.now());
    }
}

// FUTURO: LanguagesSyncCompleted, GenresSyncCompleted, CompaniesSyncCompleted, etc.
```

#### 1.9 Repository Ports (interfaces)

```java
// IGameRepository.java
public interface IGameRepository {
    Optional<Game> findById(GameId id);

    Game save(Game game);

    Page<Game> findByNameContaining(String name, Pageable pageable);
}

// IGameDetailRepository.java
public interface IGameDetailRepository {
    Optional<GameDetail> findByGameId(GameId gameId);

    GameDetail save(GameDetail gameDetail);
}

// IPlatformRepository.java
public interface IPlatformRepository {
    Optional<Platform> findById(PlatformId id);

    Platform save(Platform platform);

    List<Platform> findAll();

    List<Platform> saveAll(List<Platform> platforms);
}

// ISyncStateRepository.java
public interface ISyncStateRepository {
    Optional<SyncState> findByKey(SyncKey key);

    SyncState save(SyncState syncState);
}

// FUTURO: ILanguageRepository, IGenreRepository, ICompanyRepository, etc.
```

#### 1.10 Excepciones de dominio

```java
// Crear en domain/exceptions/
-GameNotFoundException.java
-InvalidGameDataException.java
-SyncStateNotFoundException.java
```

---

### **FASE 2: Capa de Aplicación** (Use Cases y DTOs)

#### 2.1 DTOs Inter-Capa

```java
// application/dto/results/
1.GameDTO.java          // Salida de use cases
2.GameDetailDTO.java    // Incluye screenshots y videos
3.PlatformDTO.java      // Plataforma (id, name, abbreviation)
4.IgdbGameDTO.java      // Respuesta parseada de IGDB (/games)
5.IgdbPlatformDTO.java  // Respuesta parseada de IGDB (/platforms)
6.SyncResultDTO.java    // Resultado de sincronización

// FUTURO: IgdbLanguageDTO, IgdbGenreDTO, IgdbCompanyDTO, LanguageDTO, GenreDTO, etc.
```

#### 2.2 Commands y Queries

```java
// application/dto/commands/
public record SyncIgdbGamesCommand(Long fromId, Integer limit) {
    public SyncIgdbGamesCommand {
        if (limit == null || limit <= 0 || limit > 500) {
            limit = 500; // Default
        }
    }
}

public record SyncPlatformsCommand() {
    // Sin parámetros - sincroniza todas las plataformas
}

// FUTURO: SyncLanguagesCommand, SyncGenresCommand, etc.

// application/dto/queries/
public record GetGameDetailQuery(Long gameId) {
}

public record SearchGamesQuery(String name, int page, int size) {
}
```

#### 2.3 Ports Externos

```java
// application/ports/IIgdbClientPort.java
public interface IIgdbClientPort {
    /**
     * Obtiene un batch de juegos desde IGDB
     * @param afterId ID del último juego sincronizado (checkpoint)
     * @param limit Máximo de juegos a obtener (máx 500)
     * @return Lista de juegos obtenidos de IGDB
     */
    List<IgdbGameDTO> fetchGamesBatch(Long afterId, int limit);

    /**
     * Obtiene todas las plataformas desde IGDB
     * @return Lista de plataformas disponibles en IGDB
     */
    List<IgdbPlatformDTO> fetchPlatforms();

    // FUTURO: 
    // List<IgdbLanguageDTO> fetchLanguages();
    // List<IgdbGenreDTO> fetchGenres();
    // List<IgdbCompanyDTO> fetchCompanies();
    // List<IgdbGameModeDTO> fetchGameModes();
    // List<IgdbThemeDTO> fetchThemes();
    // List<IgdbAgeRatingDTO> fetchAgeRatings();
}

// application/ports/IEventPublisherPort.java
public interface IEventPublisherPort {
    void publish(Object event);
}
```

#### 2.4 Use Cases

**SyncIgdbGamesUseCase.java** (más complejo)

```java

@Service
@Transactional
public class SyncIgdbGamesUseCase {
    private final IIgdbClientPort igdbClient;
    private final IGameRepository gameRepository;
    private final ISyncStateRepository syncStateRepository;
    private final IEventPublisherPort eventPublisher;

    public SyncResultDTO execute(SyncIgdbGamesCommand command) {
        // 1. Obtener checkpoint actual o usar fromId del command
        Long afterId = determineStartId(command);

        // 2. Fetch batch de IGDB
        List<IgdbGameDTO> igdbGames = igdbClient.fetchGamesBatch(afterId, command.limit());

        // 3. Convertir a dominio y hacer upsert
        igdbGames.forEach(dto -> {
            Game game = convertToGame(dto);
            gameRepository.save(game);
            eventPublisher.publish(CatalogGameUpserted.of(game.getId().value()));
        });

        // 4. Actualizar checkpoint
        Long maxId = igdbGames.stream()
                .map(IgdbGameDTO::id)
                .max(Long::compareTo)
                .orElse(afterId);
        updateCheckpoint(maxId);

        // 5. Publicar evento de batch completado
        eventPublisher.publish(new CatalogSyncBatchCompleted(
                igdbGames.size(), maxId, Instant.now()
        ));

        return new SyncResultDTO(igdbGames.size(), maxId);
    }
}
```

**SyncPlatformsFromIgdbUseCase.java** (nuevo)

```java

@Service
@Transactional
public class SyncPlatformsFromIgdbUseCase {
    private final IIgdbClientPort igdbClient;
    private final IPlatformRepository platformRepository;
    private final IEventPublisherPort eventPublisher;

    public SyncResultDTO execute(SyncPlatformsCommand command) {
        // 1. Fetch todas las plataformas de IGDB
        List<IgdbPlatformDTO> igdbPlatforms = igdbClient.fetchPlatforms();

        // 2. Convertir a dominio y hacer upsert
        List<Platform> platforms = igdbPlatforms.stream()
                .map(this::convertToPlatform)
                .toList();

        platformRepository.saveAll(platforms);

        // 3. Publicar evento de sincronización completada
        eventPublisher.publish(PlatformsSyncCompleted.of(platforms.size()));

        return new SyncResultDTO(platforms.size(), null);
    }

    private Platform convertToPlatform(IgdbPlatformDTO dto) {
        return Platform.create(
                PlatformId.of(dto.id()),
                PlatformName.of(dto.name()),
                PlatformAbbreviation.of(dto.abbreviation())
        );
    }
}
```

**GetGameDetailUseCase.java**

```java

@Service
@Transactional(readOnly = true)
public class GetGameDetailUseCase {
    private final IGameRepository gameRepository;
    private final IGameDetailRepository gameDetailRepository;

    public GameDetailDTO execute(GetGameDetailQuery query) {
        GameId gameId = GameId.of(query.gameId());

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        Optional<GameDetail> detail = gameDetailRepository.findByGameId(gameId);

        return GameDetailDTO.from(game, detail.orElse(null));
    }
}
```

**SearchGamesByNameUseCase.java**

```java

@Service
@Transactional(readOnly = true)
public class SearchGamesByNameUseCase {
    private final IGameRepository gameRepository;

    public Page<GameDTO> execute(SearchGamesQuery query) {
        Pageable pageable = PageRequest.of(query.page(), query.size());
        return gameRepository.findByNameContaining(query.name(), pageable)
                .map(GameDTO::from);
    }
}
```

---

### **FASE 3: Capa de Infraestructura - Persistencia**

#### 3.1 Entidades JPA (PostgreSQL)

**GameEntity.java**

```java

@Entity
@Table(name = "game")
public class GameEntity {
    @Id
    private Long id; // Mismo ID de IGDB

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String summary;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @ManyToMany
    @JoinTable(
            name = "game_platform",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "platform_id")
    )
    private Set<PlatformEntity> platforms = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
```

**Catálogos (PlatformEntity)**

```java

@Entity
@Table(name = "platform")
public class PlatformEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private PlatformType type;

    @Column(nullable = false)
    private String name;
}

```

**SyncStateEntity.java**

```java

@Entity
@Table(name = "sync_state")
public class SyncStateEntity {
    @Id
    @Enumerated(EnumType.STRING)
    private SyncKey key;

    @Column(nullable = false)
    private String value;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PreUpdate
    @PrePersist
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
```

#### 3.2 Spring Data JPA Repositories

```java
// Crear interfaces que extienden JpaRepository:
-GameJpaRepository.java
-PlatformJpaRepository.java
-SyncStateJpaRepository.java
```

#### 3.3 Mappers JPA

```java
// GameMapper.java - Ejemplo
@Component
public class GameMapper {
    public Game toDomain(GameEntity entity) {
        // Convertir entity → domain
        return Game.reconstitute(
                GameId.of(entity.getId()),
                GameName.of(entity.getName()),
                Summary.of(entity.getSummary()),
                CoverUrl.of(entity.getCoverUrl()),
                // mapear platforms
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public GameEntity toEntity(Game game) {
        // Convertir domain → entity
        GameEntity entity = new GameEntity();
        entity.setId(game.getId().value());
        entity.setName(game.getName().value());
        // etc.
        return entity;
    }
}
```

#### 3.4 Implementaciones de Repository Ports

```java
// GameRepositoryPostgres.java
@Component
public class GameRepositoryPostgres implements IGameRepository {
    private final GameJpaRepository jpaRepository;
    private final GameMapper mapper;

    @Override
    public Optional<Game> findById(GameId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Game save(Game game) {
        GameEntity entity = mapper.toEntity(game);
        GameEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Page<Game> findByNameContaining(String name, Pageable pageable) {
        return jpaRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(mapper::toDomain);
    }
}
```

#### 3.5 MongoDB - Documentos

**GameDetailDocument.java**

```java

@Document(collection = "game_detail")
public class GameDetailDocument {
    @Id
    private String id; // MongoDB ObjectId

    @Indexed(unique = true)
    private Long gameId; // FK lógico a Game

    private List<ScreenshotDoc> screenshots;
    private List<VideoDoc> videos;

    @Document
    public static class ScreenshotDoc {
        private String url;
        private Integer width;
        private Integer height;
    }

    @Document
    public static class VideoDoc {
        private String url;
        private String videoId;
    }
}
```

#### 3.6 Spring Data MongoDB Repository

```java
public interface GameDetailMongoRepository extends MongoRepository<GameDetailDocument, String> {
    Optional<GameDetailDocument> findByGameId(Long gameId);
}
```

#### 3.7 Implementación GameDetailRepositoryMongo

```java

@Component
public class GameDetailRepositoryMongo implements IGameDetailRepository {
    private final GameDetailMongoRepository mongoRepository;
    private final GameDetailMapper mapper;

    @Override
    public Optional<GameDetail> findByGameId(GameId gameId) {
        return mongoRepository.findByGameId(gameId.value())
                .map(mapper::toDomain);
    }

    @Override
    public GameDetail save(GameDetail gameDetail) {
        GameDetailDocument doc = mapper.toDocument(gameDetail);
        GameDetailDocument saved = mongoRepository.save(doc);
        return mapper.toDomain(saved);
    }
}
```

---

### **FASE 4: Cliente IGDB y Scheduler**

#### 4.1 Configuración de propiedades

**IgdbProperties.java**

```java

@ConfigurationProperties(prefix = "igdb")
@Data
public class IgdbProperties {
    private String clientId;
    private String apiKey;
    private String baseUrl = "https://api.igdb.com/v4";
    private int maxRetries = 3;
    private long retryDelayMs = 1000;
}
```

**application.properties**

```properties
# IGDB Configuration
igdb.client-id=${IGDB_CLIENT_ID}
igdb.api-key=${IGDB_API_KEY}
igdb.base-url=https://api.igdb.com/v4
igdb.max-retries=3
igdb.retry-delay-ms=1000
# Scheduler
catalogo.sync.cron=0 0 2 * * ? # Diario a las 2 AM
catalogo.sync.batch-size=500
catalogo.sync.enabled=true
```

#### 4.2 WebClient Configuration

```java

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient igdbWebClient(IgdbProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Client-ID", properties.getClientId())
                .defaultHeader("Authorization", "Bearer " + properties.getApiKey())
                .build();
    }
}
```

#### 4.3 IGDB Query Builder

```java

@Component
public class IgdbQueryBuilder {

    public String buildGamesBatchQuery(Long afterId, int limit) {
        StringBuilder query = new StringBuilder();
        // Campos simplificados: id, name, summary, cover, platforms, screenshots, videos
        query.append("fields id, name, summary, cover.url, ");
        query.append("platforms.*, screenshots.*, videos.*;");

        if (afterId != null) {
            query.append(" where id > ").append(afterId).append(";");
        }
        query.append(" sort id asc;");
        query.append(" limit ").append(limit).append(";");
        return query.toString();
    }
}
```

**Nota:** La API de IGDB requiere expandir los campos anidados:

- `cover.url` → URL de la imagen de portada
- `platforms.*` → Lista de plataformas disponibles
- `screenshots.*` → URLs de capturas de pantalla
- `videos.*` → URLs/IDs de videos

#### 4.4 Rate Limit Handler

```java

@Component
public class IgdbRateLimitHandler {
    private static final int MAX_RETRIES = 5;

    public <T> T executeWithRetry(Supplier<T> supplier) {
        int attempt = 0;
        while (true) {
            try {
                return supplier.get();
            } catch (WebClientResponseException e) {
                if (e.getStatusCode().value() == 429 && attempt < MAX_RETRIES) {
                    attempt++;
                    long waitTime = (long) Math.pow(2, attempt) * 1000; // Exponential backoff
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during rate limit wait", ie);
                    }
                } else {
                    throw e;
                }
            }
        }
    }
}
```

#### 4.5 IGDB HTTP Adapter

```java

@Component
public class IgdbHttpAdapter implements IIgdbClientPort {
    private final WebClient webClient;
    private final IgdbQueryBuilder queryBuilder;
    private final IgdbRateLimitHandler rateLimitHandler;

    @Override
    public List<IgdbGameDTO> fetchGamesBatch(Long afterId, int limit) {
        String query = queryBuilder.buildGamesBatchQuery(afterId, limit);

        return rateLimitHandler.executeWithRetry(() -> {
            return webClient.post()
                    .uri("/games")
                    .contentType(MediaType.TEXT_PLAIN)
                    .bodyValue(query)
                    .retrieve()
                    .bodyToFlux(IgdbGameResponseDto.class)
                    .collectList()
                    .map(this::convertToIgdbGameDTOs)
                    .block();
        });
    }

    private List<IgdbGameDTO> convertToIgdbGameDTOs(List<IgdbGameResponseDto> responses) {
        // Mapear respuesta cruda IGDB → DTO aplicación
        return responses.stream()
                .map(r -> new IgdbGameDTO(r.id(), r.name(), r.summary(), r.cover() ?.url()))
            .toList();
    }
}
```

#### 4.6 Scheduled Job

```java

@Component
@EnableScheduling
public class ScheduledSyncJob {
    private final SyncIgdbUseCase syncUseCase;

    @Value("${catalogo.sync.enabled}")
    private boolean syncEnabled;

    @Value("${catalogo.sync.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${catalogo.sync.cron}")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 5000)
    )
    public void syncIgdbIncremental() {
        if (!syncEnabled) {
            return;
        }

        log.info("Iniciando sincronización automática de IGDB");
        SyncIgdbCommand command = new SyncIgdbCommand(null, batchSize);
        SyncResultDTO result = syncUseCase.execute(command);
        log.info("Sincronización completada: {} juegos, último ID: {}",
                result.gamesProcessed(), result.lastGameId());
    }
}
```

---

### **FASE 5: API REST y Eventos**

#### 5.1 DTOs de Request/Response

**SyncIgdbRequest.java**

```java
public record SyncIgdbRequest(
        @Min(1) Long fromId,
        @Min(1) @Max(500) Integer limit
) {
}
```

**GameDetailResponse.java**

```java
public record GameDetailResponse(
        Long id,
        String name,
        String summary,
        String coverUrl,
        List<String> platforms,
        List<ScreenshotResponse> screenshots,
        List<VideoResponse> videos
) {
    public static GameDetailResponse from(GameDetailDTO dto) {
        // Mapear DTO → Response
    }
}
```

#### 5.2 REST Controller

**CatalogoController.java**

```java

@RestController
@RequestMapping("/v1/catalogo")
@Tag(name = "Catálogo", description = "Gestión del catálogo de juegos")
public class CatalogoController {
    private final SyncIgdbGamesUseCase syncGamesUseCase;
    private final SyncPlatformsFromIgdbUseCase syncPlatformsUseCase;
    private final GetGameDetailUseCase getGameDetailUseCase;
    private final SearchGamesByNameUseCase searchUseCase;

    @PostMapping("/igdb/sync")
    @Operation(summary = "Sincronizar juegos desde IGDB")
    public ResponseEntity<SyncStatusResponse> syncIgdbGames(
            @Valid @RequestBody SyncIgdbRequest request
    ) {
        SyncIgdbGamesCommand command = new SyncIgdbGamesCommand(request.fromId(), request.limit());
        SyncResultDTO result = syncGamesUseCase.execute(command);
        return ResponseEntity.ok(SyncStatusResponse.from(result));
    }

    @PostMapping("/platforms/sync")
    @Operation(summary = "Sincronizar plataformas desde IGDB")
    public ResponseEntity<SyncStatusResponse> syncPlatforms() {
        SyncPlatformsCommand command = new SyncPlatformsCommand();
        SyncResultDTO result = syncPlatformsUseCase.execute(command);
        return ResponseEntity.ok(SyncStatusResponse.from(result));
    }

    @GetMapping("/games/{id}/detail")
    @Operation(summary = "Obtener detalle completo de un juego")
    public ResponseEntity<GameDetailResponse> getGameDetail(@PathVariable Long id) {
        GetGameDetailQuery query = new GetGameDetailQuery(id);
        GameDetailDTO dto = getGameDetailUseCase.execute(query);
        return ResponseEntity.ok(GameDetailResponse.from(dto));
    }

    @GetMapping("/games")
    @Operation(summary = "Buscar juegos por nombre")
    public ResponseEntity<Page<GameSearchResponse>> searchGames(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        SearchGamesQuery query = new SearchGamesQuery(name, page, size);
        Page<GameDTO> results = searchUseCase.execute(query);
        return ResponseEntity.ok(results.map(GameSearchResponse::from));
    }
}
```

#### 5.3 RabbitMQ Configuration

**MessagingConfig.java**

```java

@Configuration
public class MessagingConfig {
    public static final String CATALOG_EXCHANGE = "catalogo.events";
    public static final String GAME_UPSERTED_ROUTING_KEY = "catalog.game.upserted";
    public static final String SYNC_COMPLETED_ROUTING_KEY = "catalog.sync.completed";

    @Bean
    public TopicExchange catalogExchange() {
        return new TopicExchange(CATALOG_EXCHANGE, true, false);
    }

    @Bean
    public Queue gameUpsertedQueue() {
        return new Queue("catalog.game.upserted.queue", true);
    }

    @Bean
    public Binding gameUpsertedBinding() {
        return BindingBuilder
                .bind(gameUpsertedQueue())
                .to(catalogExchange())
                .with(GAME_UPSERTED_ROUTING_KEY);
    }
}
```

**EventPublisherRabbitMQ.java**

```java

@Component
public class EventPublisherRabbitMQ implements IEventPublisherPort {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(Object event) {
        String routingKey = determineRoutingKey(event);
        rabbitTemplate.convertAndSend(
                MessagingConfig.CATALOG_EXCHANGE,
                routingKey,
                event
        );
        log.info("Evento publicado: {} con routing key: {}", event.getClass().getSimpleName(), routingKey);
    }

    private String determineRoutingKey(Object event) {
        return switch (event) {
            case CatalogGameUpserted e -> MessagingConfig.GAME_UPSERTED_ROUTING_KEY;
            case CatalogSyncBatchCompleted e -> MessagingConfig.SYNC_COMPLETED_ROUTING_KEY;
            default -> "catalog.unknown";
        };
    }
}
```

#### 5.4 Security Configuration

**SecurityConfig.java**

```java

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/v1/catalogo/games/**").permitAll() // Públicas (lectura)
                        .requestMatchers("/v1/catalogo/igdb/sync").hasHeader("X-User-Role", "ADMIN") // Solo admin
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}
```

---

### **FASE 6: Tests con Testcontainers**

#### 6.1 Dependencias Maven adicionales (pom.xml)

```xml
<!-- Testcontainers -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-testcontainers</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
<groupId>org.testcontainers</groupId>
<artifactId>postgresql</artifactId>
<scope>test</scope>
</dependency>
<dependency>
<groupId>org.testcontainers</groupId>
<artifactId>mongodb</artifactId>
<scope>test</scope>
</dependency>
<dependency>
<groupId>org.testcontainers</groupId>
<artifactId>rabbitmq</artifactId>
<scope>test</scope>
</dependency>
<dependency>
<groupId>org.testcontainers</groupId>
<artifactId>junit-jupiter</artifactId>
<scope>test</scope>
</dependency>
```

#### 6.2 Configuración de Tests

**application-test.properties**

```properties
# Testcontainers gestionará URLs dinámicamente
spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
# MongoDB y RabbitMQ configurados en @ServiceConnection
# Scheduler deshabilitado en tests
catalogo.sync.enabled=false
```

#### 6.3 Base Test Configuration

**TestcontainersConfiguration.java**

```java

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("catalogo_test")
                .withUsername("test")
                .withPassword("test");
    }

    @Bean
    @ServiceConnection
    MongoDBContainer mongoContainer() {
        return new MongoDBContainer("mongo:7.0")
                .withExposedPorts(27017);
    }

    @Bean
    @ServiceConnection
    RabbitMQContainer rabbitContainer() {
        return new RabbitMQContainer("rabbitmq:3.13-management-alpine");
    }
}
```

#### 6.4 Tests de Dominio

**GameNameTest.java** (ejemplo)

```java
class GameNameTest {

    @Test
    @DisplayName("Debe crear GameName válido")
    void debeCrearGameNameValido() {
        GameName name = GameName.of("The Legend of Zelda");
        assertThat(name.value()).isEqualTo("The Legend of Zelda");
    }

    @Test
    @DisplayName("Debe lanzar excepción si nombre vacío")
    void debeLanzarExcepcionSiNombreVacio() {
        assertThrows(InvalidGameDataException.class, () -> GameName.of(""));
    }

    @Test
    @DisplayName("Debe lanzar excepción si excede 200 caracteres")
    void debeLanzarExcepcionSiExcede200Caracteres() {
        String nombreLargo = "A".repeat(201);
        assertThrows(InvalidGameDataException.class, () -> GameName.of(nombreLargo));
    }
}
```

#### 6.5 Tests de Application Layer (con Mockito)

**SyncIgdbUseCaseTest.java**

```java

@ExtendWith(MockitoExtension.class)
class SyncIgdbUseCaseTest {

    @Mock
    private IIgdbClientPort igdbClient;

    @Mock
    private IGameRepository gameRepository;

    @Mock
    private ISyncStateRepository syncStateRepository;

    @Mock
    private IEventPublisherPort eventPublisher;

    @InjectMocks
    private SyncIgdbUseCase useCase;

    @Test
    @DisplayName("Debe sincronizar batch de juegos correctamente")
    void debeSincronizarBatchDeJuegos() {
        // Arrange
        SyncIgdbCommand command = new SyncIgdbCommand(null, 10);
        List<IgdbGameDTO> mockGames = List.of(
                new IgdbGameDTO(1L, "Game 1", "Summary 1", "url1"),
                new IgdbGameDTO(2L, "Game 2", "Summary 2", "url2")
        );

        when(syncStateRepository.findByKey(SyncKey.LAST_SYNCED_GAME_ID))
                .thenReturn(Optional.empty());
        when(igdbClient.fetchGamesBatch(null, 10)).thenReturn(mockGames);

        // Act
        SyncResultDTO result = useCase.execute(command);

        // Assert
        assertThat(result.gamesProcessed()).isEqualTo(2);
        assertThat(result.lastGameId()).isEqualTo(2L);
        verify(gameRepository, times(2)).save(any(Game.class));
        verify(eventPublisher, times(2)).publish(any(CatalogGameUpserted.class));
        verify(eventPublisher).publish(any(CatalogSyncBatchCompleted.class));
    }
}
```

#### 6.6 Tests de Integración con Testcontainers

**GameRepositoryPostgresIntegrationTest.java**

```java

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Testcontainers
class GameRepositoryPostgresIntegrationTest {

    @Autowired
    private IGameRepository gameRepository;

    @Test
    @DisplayName("Debe guardar y recuperar un juego")
    void debeGuardarYRecuperarJuego() {
        // Arrange
        Game game = Game.create(
                GameId.of(999L),
                GameName.of("Test Game"),
                Summary.of("Test summary"),
                CoverUrl.of("https://example.com/cover.jpg")
        );

        // Act
        Game saved = gameRepository.save(game);
        Optional<Game> retrieved = gameRepository.findById(saved.getId());

        // Assert
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName().value()).isEqualTo("Test Game");
    }
}
```

#### 6.7 Tests End-to-End

**CatalogoFlowIntegrationTest.java**

```java

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@Testcontainers
class CatalogoFlowIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private IIgdbClientPort igdbClient; // Mockeamos cliente externo

    @Test
    @DisplayName("Debe completar flujo de sincronización end-to-end")
    void debeCompletarFlujoSincronizacion() {
        // Arrange
        List<IgdbGameDTO> mockGames = List.of(
                new IgdbGameDTO(100L, "E2E Game", "Summary", "url")
        );
        when(igdbClient.fetchGamesBatch(any(), anyInt())).thenReturn(mockGames);

        // Act - Sincronizar
        SyncIgdbRequest request = new SyncIgdbRequest(null, 10);
        ResponseEntity<SyncStatusResponse> syncResponse = restTemplate
                .postForEntity("/v1/catalogo/igdb/sync", request, SyncStatusResponse.class);

        // Assert sincronización
        assertThat(syncResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(syncResponse.getBody().gamesProcessed()).isEqualTo(1);

        // Act - Consultar detalle
        ResponseEntity<GameDetailResponse> detailResponse = restTemplate
                .getForEntity("/v1/catalogo/games/100/detail", GameDetailResponse.class);

        // Assert consulta
        assertThat(detailResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(detailResponse.getBody().name()).isEqualTo("E2E Game");
    }
}
```

---

## 📝 Configuraciones Necesarias

### application.properties (desarrollo local)

```properties
# Server
spring.application.name=catalogo-service
server.port=8082
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/catalogo_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/catalogo_db
spring.data.mongodb.auto-index-creation=true
# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
# IGDB
igdb.client-id=${IGDB_CLIENT_ID}
igdb.api-key=${IGDB_API_KEY}
igdb.base-url=https://api.igdb.com/v4
igdb.max-retries=3
igdb.retry-delay-ms=1000
# Scheduler
catalogo.sync.cron=0 0 2 * * ?
catalogo.sync.batch-size=500
catalogo.sync.enabled=true
# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
# Swagger
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

### application-docker.properties

```properties
# PostgreSQL (Docker Compose)
spring.datasource.url=jdbc:postgresql://postgres:5432/catalogo_db
# MongoDB
spring.data.mongodb.uri=mongodb://mongodb:27017/catalogo_db
# RabbitMQ
spring.rabbitmq.host=rabbitmq
```

---

## 🔄 Orden de Desarrollo Sugerido (Vertical Slices)

### Enfoque recomendado: **Feature completa por iteración**

#### Iteración 1: Sincronización IGDB básica (vertical slice)

1. Domain: `Game` (id, name, summary, cover) + VOs
2. Application: `SyncIgdbUseCase` + `IIgdbClientPort`
3. Infrastructure: `GameEntity` + `GameRepositoryPostgres` + `IgdbHttpAdapter`
4. Tests: Domain VOs + Use case con mock + Repository integration test
5. **Entregable:** Sincronización funcional de juegos básicos

#### Iteración 2: Consulta de detalle (vertical slice)

1. Domain: `GameDetail` + VOs (`Screenshot`, `Video`)
2. Application: `GetGameDetailUseCase`
3. Infrastructure: `GameDetailDocument` + `GameDetailRepositoryMongo`
4. API: Endpoint `GET /v1/catalogo/games/{id}/detail`
5. Tests: End-to-end test del flujo completo
6. **Entregable:** API de consulta funcionando con MongoDB

#### Iteración 3: Plataformas y relaciones

1. Domain: `Platform` + Enum `PlatformType`
2. Infrastructure: `PlatformEntity` + tabla puente `game_platform` + mappers
3. Ampliar `SyncIgdbUseCase` para incluir plataformas
4. **Entregable:** Juegos con relación M:N a plataformas

#### Iteración 4: Búsqueda y paginación

1. Application: `SearchGamesByNameUseCase`
2. Infrastructure: Query personalizada en `GameJpaRepository`
3. API: Endpoint `GET /v1/catalogo/games?name=X`
4. **Entregable:** Búsqueda funcional

#### Iteración 5: Eventos y Scheduler

1. Infrastructure: `EventPublisherRabbitMQ` + `MessagingConfig`
2. Integrar eventos en use cases existentes
3. `ScheduledSyncJob` con `@Scheduled`
4. **Entregable:** Sistema de eventos completo

#### Iteración 6: Checkpoint y robustez

1. Domain: `SyncState` + `SyncKey` enum
2. Infrastructure: `SyncStateEntity` + Repository
3. Ampliar `SyncIgdbUseCase` con checkpoint
4. Manejo de 429 con exponential backoff
5. **Entregable:** Sistema resiliente y con checkpoint

---

## ✅ Checklist de Tareas

### Domain Layer

- [ ] Value Objects: `GameId`, `GameName`, `Summary`, `CoverUrl`
- [ ] Enums: `PlatformType`, `SyncKey`
- [ ] Entidades: `Game`, `Platform`
- [ ] Agregado: `GameDetail` con VOs `Screenshot` y `Video`
- [ ] Entidad: `SyncState`
- [ ] Eventos: `CatalogGameUpserted`, `CatalogSyncBatchCompleted`, `CatalogSyncCompleted`
- [ ] Repository Ports: `IGameRepository`, `IGameDetailRepository`, `ISyncStateRepository`
- [ ] Excepciones: `GameNotFoundException`, `InvalidGameDataException`, `SyncStateNotFoundException`

### Application Layer

- [ ] DTOs: `GameDTO`, `GameDetailDTO`, `IgdbGameDTO`, `SyncResultDTO`
- [ ] Commands: `SyncIgdbCommand`
- [ ] Queries: `GetGameDetailQuery`, `SearchGamesQuery`
- [ ] Use Cases: `SyncIgdbUseCase`, `GetGameDetailUseCase`, `SearchGamesByNameUseCase`
- [ ] Ports: `IIgdbClientPort`, `IEventPublisherPort`

### Infrastructure - Persistence

- [ ] JPA Entities: `GameEntity`, `PlatformEntity`, `SyncStateEntity`
- [ ] JPA Entity: Tabla puente `GamePlatformEntity` (M:N)
- [ ] Mongo Document: `GameDetailDocument` con embedded docs (`ScreenshotDocument`, `VideoDocument`)
- [ ] Mappers: `GameMapper`, `PlatformMapper`, `GameDetailMapper`
- [ ] JPA Repositories: `GameJpaRepository`, `PlatformJpaRepository`, `SyncStateJpaRepository`
- [ ] Mongo Repository: `GameDetailMongoRepository`
- [ ] Implementaciones: `GameRepositoryPostgres`, `GameDetailRepositoryMongo`, `SyncStateRepositoryPostgres`

### Infrastructure - IGDB Client

- [ ] `IgdbProperties` con `@ConfigurationProperties`
- [ ] `WebClientConfig` para bean de `WebClient`
- [ ] `IgdbQueryBuilder` para generar queries DSL
- [ ] `IgdbRateLimitHandler` con exponential backoff
- [ ] `IgdbHttpAdapter` implementando `IIgdbClientPort`
- [ ] `ScheduledSyncJob` con `@Scheduled`

### Infrastructure - API & Events

- [ ] DTOs Request: `SyncIgdbRequest`
- [ ] DTOs Response: `GameDetailResponse`, `GameSearchResponse`, `SyncStatusResponse`
- [ ] `CatalogoController` con 3 endpoints
- [ ] `MessagingConfig` con exchanges/queues RabbitMQ
- [ ] `EventPublisherRabbitMQ` implementando `IEventPublisherPort`
- [ ] `SecurityConfig` para validar headers del Gateway

### Tests

- [ ] Tests de Value Objects (sin Spring)
- [ ] Tests de Use Cases (con Mockito)
- [ ] Tests de repositorios (con Testcontainers)
- [ ] Tests de API (MockMvc o TestRestTemplate)
- [ ] Test end-to-end completo
- [ ] `TestcontainersConfiguration` con PostgreSQL + MongoDB + RabbitMQ

### Configuración

- [ ] `application.properties` completo
- [ ] `application-docker.properties`
- [ ] `application-test.properties`
- [ ] Variables de entorno documentadas

---

## 🚀 Comandos Útiles

### Desarrollo Local

```powershell
# Build
./mvnw clean install

# Run (requiere PostgreSQL + MongoDB + RabbitMQ locales)
./mvnw spring-boot:run

# Tests
./mvnw test

# Tests con cobertura
./mvnw test jacoco:report
```

### Docker

```powershell
# Construir imagen
docker build -t catalogo-service:latest .

# Ejecutar con Docker Compose (desde raíz del proyecto)
docker-compose up -d catalogo-service

# Ver logs
docker-compose logs -f catalogo-service
```

### Base de datos local (con Docker)

```powershell
# PostgreSQL
docker run -d --name postgres-catalogo `
  -e POSTGRES_DB=catalogo_db `
  -e POSTGRES_PASSWORD=postgres `
  -p 5432:5432 postgres:16-alpine

# MongoDB
docker run -d --name mongo-catalogo `
  -p 27017:27017 mongo:7.0

# RabbitMQ
docker run -d --name rabbitmq-catalogo `
  -p 5672:5672 -p 15672:15672 `
  rabbitmq:3.13-management-alpine
```

---

## 📚 Recursos Adicionales

### Documentación IGDB

- [API Reference](https://api-docs.igdb.com/)
- [Authentication](https://api-docs.igdb.com/#authentication)
- [Rate Limiting](https://api-docs.igdb.com/#rate-limiting)

### Tecnologías

- [Spring Boot 4.0.2 Docs](https://docs.spring.io/spring-boot/docs/4.0.2/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Data MongoDB](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- [Testcontainers](https://testcontainers.com/)

---

## ⚠️ Consideraciones Importantes del MVP

### Modelo Simplificado

Este plan implementa una **versión MVP simplificada** del catálogo:

**Incluido en MVP:**

- ✅ Información básica del juego (id, name, summary, cover)
- ✅ **Plataformas sincronizadas desde IGDB** (PS4, PS5, PC, SWITCH, XBOX, etc.)
- ✅ Contenido multimedia (screenshots, videos)
- ✅ Persistencia dual (PostgreSQL + MongoDB)
- ✅ Sincronización automática de juegos con IGDB
- ✅ Sincronización de catálogo de plataformas con IGDB
- ✅ Eventos de dominio y RabbitMQ

**Excluido del MVP (futuras iteraciones - también desde IGDB):**

- ⏳ Idiomas disponibles (Language) - `/v4/languages`
- ⏳ Modos de juego (GameMode) - `/v4/game_modes`
- ⏳ Géneros (Genre) - `/v4/genres`
- ⏳ Temas (Theme) - `/v4/themes`
- ⏳ Compañías (Developers/Publishers) - `/v4/companies`
- ⏳ Clasificación por edades (AgeRating) - `/v4/age_ratings`
- ⏳ Modos multijugador (MultiplayerMode) - `/v4/multiplayer_modes`
- ⏳ Tiempos de juego (GameTimeToBeat) - API externa HLTB
- ⏳ Fechas de lanzamiento por plataforma
- ⏳ Valoraciones y puntuaciones

### Estrategia de Sincronización de Catálogos IGDB

**Filosofía**: Todos los catálogos (Platform, Genre, Language, Company, etc.) se obtienen **dinámicamente desde IGDB**,
no se mantienen manualmente.

**Patrón de implementación** (aplicable a todos los catálogos):

1. **Entidad de dominio**: `Platform`, `Genre`, `Language`, etc.
2. **Value Objects**: `PlatformId`, `PlatformName` y `PlatformAbbreviation`
3. **Repository Port**: `IPlatformRepository`, `IGenreRepository`, etc.
4. **DTO desde IGDB**: `IgdbPlatformDTO`, `IgdbGenreDTO`, etc.
5. **Método en IIgdbClientPort**: `fetchPlatforms()`, `fetchGenres()`, etc.
6. **Caso de uso**: `SyncPlatformsFromIgdbUseCase`, `SyncGenresFromIgdbUseCase`, etc.
7. **Endpoint REST**: `POST /v1/catalogo/platforms/sync`, `POST /v1/catalogo/genres/sync`, etc.
8. **Evento de dominio**: `PlatformsSyncCompleted`, `GenresSyncCompleted`, etc.

**Ventajas**:

- 🔄 Sin mantenimiento manual de catálogos
- 🆔 IDs consistentes con IGDB (facilita relaciones)
- 📊 Información completa (logos, descripciones, slugs)
- 🚀 Extensible sin cambios de código

### Estrategia de Expansión

Para añadir **nuevos catálogos** en el futuro (Genre, Language, Company, etc.):

1. **Dominio**: Crear entidad + Value Objects siguiendo patrón de `Platform`
2. **Repository Port**: Interfaz en `/domain/repositories`
3. **IGDB DTO**: Crear `IgdbXxxDTO` en `/application/dto/results`
4. **IIgdbClientPort**: Añadir método `fetchXxx()`
5. **Adapter HTTP**: Implementar en `IgdbHttpAdapter`
6. **Caso de uso**: `SyncXxxFromIgdbUseCase`
7. **Persistencia**: JPA entity + repository
8. **Endpoint REST**: `POST /v1/catalogo/xxx/sync`
9. **Tests**: Casos de prueba con Testcontainers

**Ventaja del enfoque MVP**: Sistema funcional desde el inicio que se puede expandir incrementalmente siguiendo el mismo
patrón.

---

## 🎯 Próximos Pasos Inmediatos

1. **Empezar por Value Objects** del dominio (GameId, GameName, etc.)
2. **Crear entidad Game** con factory methods
3. **Implementar primer repository port** + implementación JPA
4. **Crear SyncIgdbUseCase** (simplificado, sin checkpoint inicial)
5. **Implementar IgdbHttpAdapter** básico
6. **Crear endpoint REST** de sincronización
7. **Escribir test de integración** completo
8. **Ampliar con MongoDB** para GameDetail
9. **Añadir eventos y scheduler**

---

**Fin del Plan de Implementación** 🎮
