package com.gamelisto.catalogo.infrastructure.in.api;

import com.gamelisto.catalogo.application.usecases.BuscarGamePorIdCommand;
import com.gamelisto.catalogo.application.usecases.BuscarGameDetailPorIdCommand;
import com.gamelisto.catalogo.application.usecases.GameCardResult;
import com.gamelisto.catalogo.application.usecases.GameResult;
import com.gamelisto.catalogo.application.usecases.GameDetailResult;
import com.gamelisto.catalogo.application.usecases.ObtenerTodosLosJuegosCommand;
import com.gamelisto.catalogo.application.usecases.PlatformResult;
import com.gamelisto.catalogo.application.usecases.ResolverJuegosPorSteamAppIdsHandle;
import com.gamelisto.catalogo.application.usecases.BuscarGameDetailPorIdHandle;
import com.gamelisto.catalogo.application.usecases.BuscarGamePorIdHandle;
import com.gamelisto.catalogo.application.usecases.ObtenerTodasLasPlatformasHandle;
import com.gamelisto.catalogo.application.usecases.ObtenerTodosLosJuegosHandle;
import com.gamelisto.catalogo.domain.PageResult;
import com.gamelisto.catalogo.infrastructure.in.api.dto.GameCardResponse;
import com.gamelisto.catalogo.infrastructure.in.api.dto.GameDetailResponse;
import com.gamelisto.catalogo.infrastructure.in.api.dto.GameResponse;
import com.gamelisto.catalogo.infrastructure.in.api.dto.PlatformResponse;
import com.gamelisto.catalogo.infrastructure.in.api.dto.ResolverJuegosSteamRequest;
import com.gamelisto.catalogo.infrastructure.in.api.dto.ResolverJuegosSteamResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/v1/catalogo")
@RequiredArgsConstructor
public class CatalogoContoller {

  private static final Logger logger = LoggerFactory.getLogger(CatalogoContoller.class);
  private static final int MAX_PAGE_SIZE = 100;

  private final BuscarGameDetailPorIdHandle getGameDetail;
  private final ObtenerTodosLosJuegosHandle obtenerTodosLosJuegos;
  private final BuscarGamePorIdHandle getGameById;
  private final ObtenerTodasLasPlatformasHandle obtenerTodasLasPlatformas;
  private final ResolverJuegosPorSteamAppIdsHandle resolverJuegosSteam;

  @GetMapping("/games")
  public ResponseEntity<List<GameCardResponse>> getAllGames(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(name = "platform", required = false) List<String> platforms) {
    int safePage = Math.max(page, 0);
    int safeSize = Math.max(1, Math.min(size, MAX_PAGE_SIZE));

    int platformFilterCount = platforms == null ? 0 : (int) platforms.stream().filter(value -> value != null && !value.isBlank()).count();

    logger.info(
        "Obteniendo listado de juegos (page: {}, size: {}, platformFilters: {})",
        safePage,
        safeSize,
        platformFilterCount);

    PageResult<GameCardResult> gamePage =
        obtenerTodosLosJuegos.execute(new ObtenerTodosLosJuegosCommand(safePage, safeSize, platforms));

    List<GameCardResponse> responses =
        gamePage.content().stream().map(GameCardResponse::from).toList();

    logger.info(
        "Retornando {} juegos paginados de {} totales (page: {}, size: {})",
        responses.size(),
        gamePage.totalElements(),
        gamePage.page(),
        gamePage.size());

    return ResponseEntity.ok()
        .header("X-Current-Page", String.valueOf(gamePage.page()))
        .header("X-Page-Size", String.valueOf(gamePage.size()))
        .header("X-Total-Count", String.valueOf(gamePage.totalElements()))
        .header("X-Total-Pages", String.valueOf(gamePage.totalPages()))
        .body(responses);
  }

  @GetMapping("/games/{id}")
  public ResponseEntity<GameResponse> getGameById(@PathVariable Long id) {
    logger.info("Obteniendo juego ID: {} desde PostgreSQL", id);

    BuscarGamePorIdCommand query = new BuscarGamePorIdCommand(id);
    GameResult dto = getGameById.execute(query);

    return ResponseEntity.ok(GameResponse.from(dto));
  }

  @GetMapping("/games/{id}/detail")
  public ResponseEntity<GameDetailResponse> getGameDetailOnly(@PathVariable Long id) {
    logger.info("Obteniendo Game Detail (MongoDB) del juego ID: {}", id);

    BuscarGameDetailPorIdCommand query = new BuscarGameDetailPorIdCommand(id);
    GameDetailResult detailDTO = getGameDetail.execute(query);

    return ResponseEntity.ok(GameDetailResponse.from(detailDTO));
  }

  @GetMapping("/platforms")
  public ResponseEntity<List<PlatformResponse>> getAllPlatforms() {
    logger.info("Obteniendo listado de plataformas");

    List<PlatformResult> platforms = obtenerTodasLasPlatformas.execute();
    List<PlatformResponse> responses = platforms.stream().map(PlatformResponse::from).toList();

    return ResponseEntity.ok(responses);
  }

  @PostMapping("/games/steam/resolve")
  public ResponseEntity<ResolverJuegosSteamResponse> resolveSteamGames(
      @Valid @RequestBody ResolverJuegosSteamRequest request) {
    logger.info(
        "Resolviendo {} appIds de Steam contra el catalogo",
        request.steamAppIds() != null ? request.steamAppIds().size() : 0);

    return ResponseEntity.ok(
        ResolverJuegosSteamResponse.from(resolverJuegosSteam.execute(request.steamAppIds())));
  }
}
