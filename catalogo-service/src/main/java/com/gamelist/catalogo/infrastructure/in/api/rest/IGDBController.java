package com.gamelist.catalogo.infrastructure.in.api.rest;

import com.gamelist.catalogo.application.dto.commands.SyncPlatformsCommand;
import com.gamelist.catalogo.application.dto.results.SyncResultDTO;
import com.gamelist.catalogo.application.usecases.SyncIgdbGamesUseCase;
import com.gamelist.catalogo.application.usecases.SyncPlatformsFromIgdbUseCase;
import com.gamelist.catalogo.infrastructure.out.api.dto.response.SyncStatusResponse;
import com.gamelist.catalogo.shared.config.IgdbProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/catalogo")
@Tag(name = "Catálogo de Videojuegos", description = "Ingesta y gestión de videojuegos desde IGDB")
@RequiredArgsConstructor
@Slf4j
public class IGDBController {

  private final SyncIgdbGamesUseCase syncGamesUseCase;
  private final SyncPlatformsFromIgdbUseCase syncPlatformsUseCase;
  private final IgdbProperties igdbProperties;

  @PostMapping("/sync/games")
  @Operation(
      summary = "Sincronizar juegos desde IGDB",
      description = "Ejecuta sincronización manual de juegos desde IGDB API")
  @ApiResponse(
      responseCode = "200",
      description = "Sincronización completada",
      content = @Content(schema = @Schema(implementation = SyncStatusResponse.class)))
  public ResponseEntity<SyncStatusResponse> syncGames() {
    log.info("Iniciando sincronización manual de juegos desde IGDB");

    int limit = igdbProperties.getBatchSize();
    SyncResultDTO result = syncGamesUseCase.execute(limit);

    SyncStatusResponse response =
        SyncStatusResponse.from(result, "Sincronización de juegos completada");

    log.info(
        "Sincronización completada: {} juegos, último ID: {}",
        result.totalSynced(),
        result.lastId());

    return ResponseEntity.ok(response);
  }

  @PostMapping("/sync/platforms")
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
}
