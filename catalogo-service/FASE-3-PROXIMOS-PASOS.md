# 🚀 Próximos Pasos - FASE 3: Infraestructura (Adapters)

## 📋 Objetivo

Implementar los **adapters de infraestructura** que conectan la capa de aplicación con servicios externos:

- IGDB API (HTTP Client)
- RabbitMQ (Event Publisher) - Los eventos serán consumidos por **search-service**
- REST API (Controllers)
- Scheduler (Sincronización automática)

---

## 🔍 Nota Importante: Integración con search-service

**catalogo-service NO implementa búsqueda avanzada**. Esta responsabilidad está delegada a **search-service** (
OpenSearch).

### Flujo de Indexación:

```
catalogo-service (publica eventos)
    ↓ CatalogGameUpserted
RabbitMQ (catalog.events)
    ↓ 
search-service (escucha eventos)
    ↓
OpenSearch (indexa juegos)
```

### Endpoints de Búsqueda:

- ❌ `catalogo-service`: NO tiene endpoints de búsqueda full-text
- ✅ `search-service`: `/v1/search/games?q=zelda` (OpenSearch)
- ✅ `graphql-bff`: Agrega datos de search-service + catalogo-service

**Ver detalles:** `INTEGRACION-SERVICIOS.md`

---

## 🎯 Orden de Implementación

### **PASO 1: Configuración y Properties**

#### 1.1 IgdbProperties

**Ubicación:** `infrastructure/config/IgdbProperties.java`

```java

@Configuration
@ConfigurationProperties(prefix = "igdb")
@Validated
public class IgdbProperties {
    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;

    @NotBlank
    private String baseUrl = "https://api.igdb.com/v4";

    private int timeout = 30000; // 30 segundos

    private int batchSize = 500;

    // getters y setters
}
```

**application.properties:**

```properties
# IGDB API Configuration
igdb.client-id=${IGDB_CLIENT_ID:your-client-id}
igdb.client-secret=${IGDB_CLIENT_SECRET:your-client-secret}
igdb.base-url=https://api.igdb.com/v4
igdb.timeout=30000
igdb.batch-size=500
```

#### 1.2 WebClientConfig

**Ubicación:** `infrastructure/config/WebClientConfig.java`

```java

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient igdbWebClient(IgdbProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Client-ID", properties.getClientId())
                .build();
    }
}
```

---

### **PASO 2: Adapter HTTP para IGDB**

#### 2.1 DTOs de Respuesta de IGDB

**Ubicación:** `infrastructure/igdb/dto/`

```java
// IgdbGameResponseDto.java
public record IgdbGameResponseDto(
                Long id,
                String name,
                String summary,
                IgdbCoverDto cover,
                List<Long> platforms
        ) {
}

// IgdbCoverDto.java
public record IgdbCoverDto(
        String url,
        Integer width,
        Integer height
) {
}

// IgdbPlatformResponseDto.java
public record IgdbPlatformResponseDto(
        Long id,
        String name,
        String abbreviation,
        String platform_logo,
        Integer category
) {
}
```

#### 2.2 IgdbQueryBuilder

**Ubicación:** `infrastructure/igdb/IgdbQueryBuilder.java`

```java

@Component
public class IgdbQueryBuilder {

    public String buildGamesQuery(Long afterId, int limit) {
        StringBuilder query = new StringBuilder();
        query.append("fields id,name,summary,cover.url,platforms;");

        if (afterId != null) {
            query.append("where id > ").append(afterId).append(";");
        }

        query.append("limit ").append(limit).append(";");
        query.append("sort id asc;");

        return query.toString();
    }

    public String buildPlatformsQuery() {
        return "fields id,name,abbreviation,platform_logo,category; limit 500;";
    }
}
```

#### 2.3 IgdbRateLimitHandler

**Ubicación:** `infrastructure/igdb/IgdbRateLimitHandler.java`

```java

@Component
@Slf4j
public class IgdbRateLimitHandler {

    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF = 1000L; // 1 segundo

    public <T> T executeWithRetry(Supplier<T> operation) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                return operation.get();
            } catch (WebClientResponseException e) {
                if (e.getStatusCode().value() == 429) { // Rate limit
                    attempt++;
                    long backoff = INITIAL_BACKOFF * (long) Math.pow(2, attempt - 1);
                    log.warn("Rate limit alcanzado. Reintentando en {}ms (intento {}/{})",
                            backoff, attempt, MAX_RETRIES);
                    try {
                        Thread.sleep(backoff);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupción durante rate limit backoff", ie);
                    }
                } else {
                    throw e;
                }
            }
        }
        throw new RuntimeException("Máximo de reintentos alcanzado para IGDB API");
    }
}
```

#### 2.4 IgdbHttpAdapter (Implementación del Port)

**Ubicación:** `infrastructure/igdb/IgdbHttpAdapter.java`

```java

@Component
@RequiredArgsConstructor
@Slf4j
public class IgdbHttpAdapter implements IIgdbClientPort {

    private final WebClient igdbWebClient;
    private final IgdbQueryBuilder queryBuilder;
    private final IgdbRateLimitHandler rateLimitHandler;
    private final IgdbProperties properties;

    @Override
    public List<IgdbGameDTO> fetchGamesBatch(Long afterId, int limit) {
        log.info("Obteniendo batch de juegos desde IGDB (afterId: {}, limit: {})", afterId, limit);

        String query = queryBuilder.buildGamesQuery(afterId, limit);

        return rateLimitHandler.executeWithRetry(() -> {
            List<IgdbGameResponseDto> response = igdbWebClient.post()
                    .uri("/games")
                    .header("Authorization", "Bearer " + getAccessToken())
                    .bodyValue(query)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<IgdbGameResponseDto>>() {
                    })
                    .block();

            return convertToGameDTOs(response);
        });
    }

    @Override
    public List<IgdbPlatformDTO> fetchPlatforms() {
        log.info("Obteniendo plataformas desde IGDB");

        String query = queryBuilder.buildPlatformsQuery();

        return rateLimitHandler.executeWithRetry(() -> {
            List<IgdbPlatformResponseDto> response = igdbWebClient.post()
                    .uri("/platforms")
                    .header("Authorization", "Bearer " + getAccessToken())
                    .bodyValue(query)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<IgdbPlatformResponseDto>>() {
                    })
                    .block();

            return convertToPlatformDTOs(response);
        });
    }

    private String getAccessToken() {
        // TODO: Implementar obtención y caché de token OAuth2
        // Por ahora, usar client credentials flow de IGDB
        return "YOUR_ACCESS_TOKEN";
    }

    private List<IgdbGameDTO> convertToGameDTOs(List<IgdbGameResponseDto> responses) {
        return responses.stream()
                .map(r -> new IgdbGameDTO(
                        r.id(),
                        r.name(),
                        r.summary(),
                        r.cover() != null ? r.cover().url() : null,
                        r.platforms() != null ? r.platforms() : List.of()
                ))
                .toList();
    }

    private List<IgdbPlatformDTO> convertToPlatformDTOs(List<IgdbPlatformResponseDto> responses) {
        return responses.stream()
                .map(r -> new IgdbPlatformDTO(r.id(), r.name(), r.abbreviation()))
                .toList();
    }
}
```

---

### **PASO 3: Event Publisher para RabbitMQ**

#### 3.1 MessagingConfig

**Ubicación:** `infrastructure/messaging/config/MessagingConfig.java`

```java

@Configuration
public class MessagingConfig {

    public static final String CATALOG_EXCHANGE = "catalog.events";
    public static final String GAME_UPSERTED_QUEUE = "catalog.game.upserted";
    public static final String SYNC_COMPLETED_QUEUE = "catalog.sync.completed";

    @Bean
    public TopicExchange catalogExchange() {
        return new TopicExchange(CATALOG_EXCHANGE);
    }

    @Bean
    public Queue gameUpsertedQueue() {
        return new Queue(GAME_UPSERTED_QUEUE, true);
    }

    @Bean
    public Queue syncCompletedQueue() {
        return new Queue(SYNC_COMPLETED_QUEUE, true);
    }

    @Bean
    public Binding gameUpsertedBinding(Queue gameUpsertedQueue, TopicExchange catalogExchange) {
        return BindingBuilder.bind(gameUpsertedQueue)
                .to(catalogExchange)
                .with("catalog.game.*");
    }

    @Bean
    public Binding syncCompletedBinding(Queue syncCompletedQueue, TopicExchange catalogExchange) {
        return BindingBuilder.bind(syncCompletedQueue)
                .to(catalogExchange)
                .with("catalog.sync.*");
    }
}
```

#### 3.2 EventPublisherRabbitMQ

**Ubicación:** `infrastructure/messaging/publishers/EventPublisherRabbitMQ.java`

```java

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisherRabbitMQ implements IEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(Object event) {
        String routingKey = determineRoutingKey(event);
        log.info("Publicando evento: {} con routing key: {}", event.getClass().getSimpleName(), routingKey);

        rabbitTemplate.convertAndSend(
                MessagingConfig.CATALOG_EXCHANGE,
                routingKey,
                event
        );
    }

    private String determineRoutingKey(Object event) {
        return switch (event) {
            case CatalogGameUpserted e -> "catalog.game.upserted";
            case CatalogSyncBatchCompleted e -> "catalog.sync.batch.completed";
            case CatalogSyncCompleted e -> "catalog.sync.completed";
            case PlatformsSyncCompleted e -> "catalog.sync.platforms.completed";
            default -> "catalog.event.unknown";
        };
    }
}
```

---

### **PASO 4: REST Controllers**

#### 4.1 Request/Response DTOs

**Ubicación:** `infrastructure/api/dto/`

```java
// request/SyncIgdbRequest.java
public record SyncIgdbRequest(
                Long fromId,
                @Min(1) @Max(500) Integer limit
        ) {
}

// response/GameDetailResponse.java
public record GameDetailResponse(
        Long id,
        String name,
        String summary,
        String coverUrl,
        Set<PlatformResponse> platforms,
        List<ScreenshotResponse> screenshots,
        List<VideoResponse> videos
) {
}

// response/SyncStatusResponse.java
public record SyncStatusResponse(
        int totalSynced,
        Long lastId,
        String message
) {
}
```

#### 4.2 CatalogoController

**Ubicación:** `infrastructure/api/rest/CatalogoController.java`

```java

@RestController
@RequestMapping("/v1/catalogo")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CatalogoController {

    private final SyncIgdbGamesUseCase syncGamesUseCase;
    private final SyncPlatformsFromIgdbUseCase syncPlatformsUseCase;
    private final GetGameDetailUseCase getGameDetailUseCase;
    private final SearchGamesByNameUseCase searchGamesUseCase;

    @PostMapping("/sync/games")
    public ResponseEntity<SyncStatusResponse> syncGames(@RequestBody @Valid SyncIgdbRequest request) {
        log.info("Iniciando sincronización de juegos desde IGDB");

        SyncIgdbGamesCommand command = new SyncIgdbGamesCommand(
                request.fromId(),
                request.limit() != null ? request.limit() : 500
        );

        SyncResultDTO result = syncGamesUseCase.execute(command);

        SyncStatusResponse response = new SyncStatusResponse(
                result.totalSynced(),
                result.lastId(),
                "Sincronización de juegos completada"
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sync/platforms")
    public ResponseEntity<SyncStatusResponse> syncPlatforms() {
        log.info("Iniciando sincronización de plataformas desde IGDB");

        SyncPlatformsCommand command = new SyncPlatformsCommand();
        SyncResultDTO result = syncPlatformsUseCase.execute(command);

        SyncStatusResponse response = new SyncStatusResponse(
                result.totalSynced(),
                null,
                "Sincronización de plataformas completada"
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/games/{id}")
    public ResponseEntity<GameDetailResponse> getGameDetail(@PathVariable Long id) {
        log.info("Obteniendo detalle del juego ID: {}", id);

        GetGameDetailQuery query = new GetGameDetailQuery(id);
        GetGameDetailUseCase.GameWithDetailDTO result = getGameDetailUseCase.executeComplete(query);

        GameDetailResponse response = convertToResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/games/search")
    public ResponseEntity<Page<GameResponse>> searchGames(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Buscando juegos con nombre: '{}'", name);

        SearchGamesQuery query = new SearchGamesQuery(name, page, size);
        Page<GameDTO> results = searchGamesUseCase.execute(query);

        Page<GameResponse> response = results.map(this::convertToGameResponse);
        return ResponseEntity.ok(response);
    }

    // Métodos de conversión privados...
}
```

---

### **PASO 5: Scheduled Jobs**

#### 5.1 ScheduledSyncJob

**Ubicación:** `infrastructure/scheduler/ScheduledSyncJob.java`

```java

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledSyncJob {

    private final SyncIgdbGamesUseCase syncGamesUseCase;
    private final SyncPlatformsFromIgdbUseCase syncPlatformsUseCase;

    // Ejecutar cada 6 horas
    @Scheduled(cron = "0 0 */6 * * *")
    public void syncGamesIncremental() {
        log.info("Iniciando sincronización automática de juegos");

        try {
            SyncIgdbGamesCommand command = new SyncIgdbGamesCommand(null, 500);
            SyncResultDTO result = syncGamesUseCase.execute(command);

            log.info("Sincronización automática completada: {} juegos, último ID: {}",
                    result.totalSynced(), result.lastId());
        } catch (Exception e) {
            log.error("Error en sincronización automática de juegos", e);
        }
    }

    // Ejecutar una vez al día (a las 3 AM)
    @Scheduled(cron = "0 0 3 * * *")
    public void syncPlatformsDaily() {
        log.info("Iniciando sincronización diaria de plataformas");

        try {
            SyncPlatformsCommand command = new SyncPlatformsCommand();
            SyncResultDTO result = syncPlatformsUseCase.execute(command);

            log.info("Sincronización de plataformas completada: {} plataformas",
                    result.totalSynced());
        } catch (Exception e) {
            log.error("Error en sincronización de plataformas", e);
        }
    }
}
```

**Activar scheduling en Application:**

```java

@SpringBootApplication
@EnableScheduling
public class CatalogoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatalogoServiceApplication.class, args);
    }
}
```

---

## ✅ Checklist de Implementación

### Configuración

- [ ] `IgdbProperties.java`
- [ ] `WebClientConfig.java`
- [ ] Agregar propiedades en `application.properties`

### IGDB Adapter

- [ ] DTOs de respuesta (`IgdbGameResponseDto`, `IgdbPlatformResponseDto`)
- [ ] `IgdbQueryBuilder.java`
- [ ] `IgdbRateLimitHandler.java`
- [ ] `IgdbHttpAdapter.java`
- [ ] OAuth2 token management (opcional para MVP)

### Event Publisher

- [ ] `MessagingConfig.java`
- [ ] `EventPublisherRabbitMQ.java`

### REST API

- [ ] Request DTOs
- [ ] Response DTOs
- [ ] `CatalogoController.java`

### Scheduler

- [ ] `ScheduledSyncJob.java`
- [ ] `@EnableScheduling` en Application

---

## 🧪 Testing de FASE 3

```java
// IgdbHttpAdapterTest.java - Con WireMock
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class IgdbHttpAdapterTest {

    @Autowired
    private IgdbHttpAdapter adapter;

    @Test
    void debeFetchearJuegosCorrectamente() {
        // Stub IGDB API response
        stubFor(post(urlEqualTo("/games"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\":1,\"name\":\"Test Game\"}]")));

        // When
        List<IgdbGameDTO> games = adapter.fetchGamesBatch(null, 10);

        // Then
        assertThat(games).hasSize(1);
        assertThat(games.get(0).name()).isEqualTo("Test Game");
    }
}
```

---

## 📊 Estimación de Esfuerzo

| Componente       | Tiempo Estimado |
|------------------|-----------------|
| Configuración    | 30 min          |
| IGDB Adapter     | 2-3 horas       |
| Event Publisher  | 1 hora          |
| REST Controllers | 2 horas         |
| Scheduler        | 30 min          |
| Tests            | 2 horas         |
| **TOTAL**        | **8-9 horas**   |

---

## 🚀 Comando para Iniciar

```bash
# 1. Configurar variables de entorno
export IGDB_CLIENT_ID=your_client_id
export IGDB_CLIENT_SECRET=your_client_secret

# 2. Iniciar PostgreSQL + MongoDB + RabbitMQ
docker-compose up -d

# 3. Compilar y ejecutar
./mvnw spring-boot:run
```

---

**Siguiente:** Una vez completada la FASE 3, proceder con **FASE 4: Tests de Integración con Testcontainers**.

---

**Creado:** 2026-02-06
