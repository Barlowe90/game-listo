package com.gamelist.catalogo.infrastructure.in.api;

import com.gamelist.catalogo.application.dto.out.SyncResultDTO;
import com.gamelist.catalogo.application.usecases.SyncGamesFromIGDBUseCase;
import com.gamelist.catalogo.application.usecases.SyncPlatformsFromIGDBUseCase;
import com.gamelist.catalogo.infrastructure.in.api.dto.SyncGamesRequest;
import com.gamelist.catalogo.infrastructure.out.dto.SyncStatusResponse;
import com.gamelist.catalogo.shared.config.IgdbProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/catalogo")
@Tag(name = "Catálogo de Videojuegos", description = "Ingesta y gestión de videojuegos desde IGDB")
@RequiredArgsConstructor
public class IGDBController {

  private final SyncGamesFromIGDBUseCase syncGamesUseCase;
  private final SyncPlatformsFromIGDBUseCase syncPlatformsUseCase;
  private final IgdbProperties igdbProperties;

  @PostMapping("/sync/games")
  @Operation(summary = "Sincronizar juegos desde IGDB")
  public ResponseEntity<SyncStatusResponse> syncGames(
      @RequestBody(required = false) SyncGamesRequest request) {

    int limit =
        (request != null && request.limit() != null)
            ? request.limit()
            : igdbProperties.getBatchSize();
    SyncResultDTO result = syncGamesUseCase.execute(limit);

    SyncStatusResponse response =
        SyncStatusResponse.from(result, "Sincronización de juegos completada");

    return ResponseEntity.ok(response);
  }

  @PostMapping("/sync/platforms")
  @Operation(summary = "Sincronizar plataformas desde IGDB")
  public ResponseEntity<SyncStatusResponse> syncPlatforms() {

    SyncResultDTO result = syncPlatformsUseCase.execute();

    SyncStatusResponse response =
        SyncStatusResponse.from(result, "Sincronización de plataformas completada");

    return ResponseEntity.ok(response);
  }
}
