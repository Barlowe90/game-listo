package com.gamelisto.catalogo.infrastructure.in.api;

import com.gamelisto.catalogo.application.dto.out.SyncResultDTO;
import com.gamelisto.catalogo.application.usecases.SyncGamesFromIGDBHandle;
import com.gamelisto.catalogo.application.usecases.SyncPlatformsFromIGDBHandle;
import com.gamelisto.catalogo.infrastructure.in.api.dto.SyncGamesRequest;
import com.gamelisto.catalogo.infrastructure.out.dto.SyncStatusResponse;
import com.gamelisto.catalogo.shared.config.IgdbProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/catalogo")
@RequiredArgsConstructor
public class IGDBController {

  private final SyncGamesFromIGDBHandle syncGames;
  private final SyncPlatformsFromIGDBHandle syncPlatforms;
  private final IgdbProperties igdbProperties;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/sync/games")
  public ResponseEntity<SyncStatusResponse> syncGames(
      @RequestBody(required = false) SyncGamesRequest request) {

    int limit =
        (request != null && request.limit() != null)
            ? request.limit()
            : igdbProperties.getBatchSize();
    SyncResultDTO result = syncGames.execute(limit);

    SyncStatusResponse response =
        SyncStatusResponse.from(result, "Sincronización de juegos completada");

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/sync/platforms")
  public ResponseEntity<SyncStatusResponse> syncPlatforms() {

    SyncResultDTO result = syncPlatforms.execute();

    SyncStatusResponse response =
        SyncStatusResponse.from(result, "Sincronización de plataformas completada");

    return ResponseEntity.ok(response);
  }
}
