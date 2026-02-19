package com.gamelist.catalogo.infrastructure.api.rest;

import com.gamelist.catalogo.application.dto.commands.SyncIgdbGamesCommand;
import com.gamelist.catalogo.application.dto.commands.SyncPlatformsCommand;
import com.gamelist.catalogo.application.dto.queries.GetGameDetailQuery;
import com.gamelist.catalogo.application.dto.results.PlatformDTO;
import com.gamelist.catalogo.application.dto.results.SyncResultDTO;
import com.gamelist.catalogo.application.usecases.GetGameDetailUseCase;
import com.gamelist.catalogo.application.usecases.SyncIgdbGamesUseCase;
import com.gamelist.catalogo.application.usecases.SyncPlatformsFromIgdbUseCase;
import com.gamelist.catalogo.domain.catalog.Platform;
import com.gamelist.catalogo.domain.catalog.PlatformId;
import com.gamelist.catalogo.domain.repositories.IPlatformRepository;
import com.gamelist.catalogo.infrastructure.api.dto.request.SyncIgdbRequest;
import com.gamelist.catalogo.infrastructure.api.dto.response.GameDetailResponse;
import com.gamelist.catalogo.infrastructure.api.dto.response.PlatformResponse;
import com.gamelist.catalogo.infrastructure.api.dto.response.SyncStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/catalogo")
@Tag(name = "Catálogo de Videojuegos", description = "Ingesta y gestión de videojuegos desde IGDB")
@RequiredArgsConstructor
@Slf4j
public class CatalogoContoller {

  private final SyncIgdbGamesUseCase syncGamesUseCase;
  private final SyncPlatformsFromIgdbUseCase syncPlatformsUseCase;
  private final GetGameDetailUseCase getGameDetailUseCase;
  private final IPlatformRepository platformRepository;

  @GetMapping("/health")
  @Operation(summary = "Health check", description = "Verifica que el servicio está activo")
  public ResponseEntity<String> health() {
    return ResponseEntity.ok("Catalogo Service is running!");
  }

  @PostMapping("/sync/games")
  @Operation(
      summary = "Sincronizar juegos desde IGDB",
      description = "Ejecuta sincronización manual de juegos desde IGDB API")
  @ApiResponse(
      responseCode = "200",
      description = "Sincronización completada",
      content = @Content(schema = @Schema(implementation = SyncStatusResponse.class)))
  public ResponseEntity<SyncStatusResponse> syncGames(
      @Valid @RequestBody(required = false) SyncIgdbRequest request) {
    log.info("Iniciando sincronización manual de juegos desde IGDB");

    // Usar valores por defecto si no se envía request body
    Long fromId = request != null ? request.fromId() : null;
    Integer limit = request != null && request.limit() != null ? request.limit() : 500;

    SyncIgdbGamesCommand command = new SyncIgdbGamesCommand(fromId, limit);
    SyncResultDTO result = syncGamesUseCase.execute(command);

    SyncStatusResponse response =
        SyncStatusResponse.from(result, "Sincronización de juegos completada");

    log.info(
        "Sincronización completada: {} juegos, último ID: {}",
        result.totalSynced(),
        result.lastId());

    return ResponseEntity.ok(response);
  }

  @PostMapping("/igdb/sync/platforms")
  @Operation(
      summary = "Sincronizar plataformas desde IGDB",
      description = "Ejecuta sincronización manual de plataformas desde IGDB API")
  @ApiResponse(
      responseCode = "200",
      description = "Sincronización completada",
      content = @Content(schema = @Schema(implementation = SyncStatusResponse.class)))
  public ResponseEntity<SyncStatusResponse> syncPlatforms() {
    log.info("Iniciando sincronización manual de plataformas desde IGDB");

    SyncPlatformsCommand command = new SyncPlatformsCommand();
    SyncResultDTO result = syncPlatformsUseCase.execute(command);

    SyncStatusResponse response =
        SyncStatusResponse.from(result, "Sincronización de plataformas completada");

    log.info("Sincronización de plataformas completada: {} plataformas", result.totalSynced());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/games/{id}")
  @Operation(
      summary = "Obtener detalle completo de un juego",
      description =
          "Retorna información básica del juego junto con multimedia (screenshots y videos)")
  @ApiResponse(
      responseCode = "200",
      description = "Juego encontrado",
      content = @Content(schema = @Schema(implementation = GameDetailResponse.class)))
  @ApiResponse(responseCode = "404", description = "Juego no encontrado")
  public ResponseEntity<GameDetailResponse> getGameDetail(@PathVariable Long id) {
    log.info("Obteniendo detalle del juego ID: {}", id);

    GetGameDetailQuery query = new GetGameDetailQuery(id);
    GetGameDetailUseCase.GameWithDetailDTO result = getGameDetailUseCase.executeComplete(query);

    GameDetailResponse response = GameDetailResponse.from(result);

    return ResponseEntity.ok(response);
  }

  // ─────────────────────────────────────────────────────────────────────────────
  // PLATAFORMAS
  // ─────────────────────────────────────────────────────────────────────────────

  @GetMapping("/platforms")
  @Operation(
      summary = "Listar todas las plataformas",
      description = "Retorna el catálogo completo de plataformas sincronizadas desde IGDB")
  @ApiResponse(
      responseCode = "200",
      description = "Lista de plataformas",
      content =
          @Content(array = @ArraySchema(schema = @Schema(implementation = PlatformResponse.class))))
  public ResponseEntity<List<PlatformResponse>> getAllPlatforms() {
    log.info("Obteniendo listado de plataformas");

    List<PlatformResponse> platforms =
        platformRepository.findAll().stream()
            .map(
                p ->
                    PlatformResponse.from(
                        new PlatformDTO(
                            p.getId().value(), p.getName().value(), p.getAbbreviation().value())))
            .toList();

    return ResponseEntity.ok(platforms);
  }

  @GetMapping("/platforms/{id}")
  @Operation(
      summary = "Obtener una plataforma por ID",
      description = "Retorna los datos de una plataforma específica por su ID de IGDB")
  @ApiResponse(
      responseCode = "200",
      description = "Plataforma encontrada",
      content = @Content(schema = @Schema(implementation = PlatformResponse.class)))
  @ApiResponse(responseCode = "404", description = "Plataforma no encontrada")
  public ResponseEntity<PlatformResponse> getPlatformById(@PathVariable Long id) {
    log.info("Obteniendo plataforma ID: {}", id);

    Platform platform =
        platformRepository
            .findById(PlatformId.of(id))
            .orElseThrow(() -> new GameNotFoundException("Plataforma no encontrada con ID: " + id));

    PlatformResponse response =
        PlatformResponse.from(
            new PlatformDTO(
                platform.getId().value(),
                platform.getName().value(),
                platform.getAbbreviation().value()));

    return ResponseEntity.ok(response);
  }
}
