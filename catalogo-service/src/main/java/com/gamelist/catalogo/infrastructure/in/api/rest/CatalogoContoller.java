package com.gamelist.catalogo.infrastructure.in.api.rest;

import com.gamelist.catalogo.application.dto.queries.GetGameDetailQuery;
import com.gamelist.catalogo.application.dto.results.PlatformDTO;
import com.gamelist.catalogo.application.usecases.GetGameDetailUseCase;
import com.gamelist.catalogo.domain.exceptions.EntityNotFoundException;
import com.gamelist.catalogo.domain.game.GameId;
import com.gamelist.catalogo.domain.repositories.IGameRepository;
import com.gamelist.catalogo.domain.repositories.IPlatformRepository;
import com.gamelist.catalogo.infrastructure.out.api.dto.response.GameDetailResponse;
import com.gamelist.catalogo.infrastructure.out.api.dto.response.GameResponse;
import com.gamelist.catalogo.infrastructure.out.api.dto.response.PlatformResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/catalogo")
@Tag(name = "Catálogo de Videojuegos", description = "Gestión de videojuegos")
@RequiredArgsConstructor
@Slf4j
public class CatalogoContoller {

  private final GetGameDetailUseCase getGameDetailUseCase;
  private final IPlatformRepository platformRepository;
  private final IGameRepository gameRepository;

  @GetMapping("/games")
  @Operation(
      summary = "Listar todos los juegos",
      description = "Retorna lista paginada de juegos (solo datos básicos, sin multimedia)")
  @ApiResponse(
      responseCode = "200",
      description = "Lista de juegos",
      content =
          @Content(array = @ArraySchema(schema = @Schema(implementation = GameResponse.class))))
  public ResponseEntity<List<GameResponse>> getAllGames(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    log.info("Obteniendo listado de juegos (page: {}, size: {})", page, size);

    // Obtener todos los juegos (en producción usar Pageable)
    List<com.gamelist.catalogo.domain.game.Game> games = gameRepository.findAll();

    // Paginación manual simple (para TFG)
    int start = page * size;
    int end = Math.min(start + size, games.size());

    if (start >= games.size()) {
      return ResponseEntity.ok(List.of());
    }

    List<GameResponse> response =
        games.subList(start, end).stream().map(this::convertToGameResponse).toList();

    log.info("Retornando {} juegos (total: {})", response.size(), games.size());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/games/{id}")
  @Operation(
      summary = "Obtener un juego por ID",
      description = "Retorna información del juego desde PostgreSQL")
  @ApiResponse(
      responseCode = "200",
      description = "Juego encontrado",
      content = @Content(schema = @Schema(implementation = GameResponse.class)))
  @ApiResponse(responseCode = "404", description = "Juego no encontrado")
  public ResponseEntity<GameResponse> getGameById(@PathVariable Long id) {
    log.info("Obteniendo juego ID: {} desde PostgreSQL", id);

    com.gamelist.catalogo.domain.game.Game game =
        gameRepository
            .findById(GameId.of(id))
            .orElseThrow(() -> new EntityNotFoundException("Juego no encontrado con ID: " + id));

    return ResponseEntity.ok(convertToGameResponse(game));
  }

  @GetMapping("/games/{id}/detail")
  @Operation(
      summary = "Obtener game detail de MongoDB",
      description = "Retorna información multimedia del juego desde MongoDB")
  @ApiResponse(
      responseCode = "200",
      description = "Game Detail encontrado",
      content = @Content(schema = @Schema(implementation = GameDetailResponse.class)))
  @ApiResponse(responseCode = "404", description = "Juego no encontrado")
  public ResponseEntity<GameDetailResponse> getGameDetailOnly(@PathVariable Long id) {
    log.info("Obteniendo Game Detail (MongoDB) del juego ID: {}", id);

    GetGameDetailQuery query = new GetGameDetailQuery(id);
    com.gamelist.catalogo.application.dto.results.GameDetailDTO detailDTO =
        getGameDetailUseCase.execute(query);

    return ResponseEntity.ok(GameDetailResponse.from(detailDTO));
  }

  @GetMapping("/platforms")
  @Operation(
      summary = "Listar todas las plataformas",
      description = "Retorna el catálogo completo de plataformas")
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

  private GameResponse convertToGameResponse(com.gamelist.catalogo.domain.game.Game game) {
    return new GameResponse(
        game.getId().value(),
        game.getName().value(),
        game.getSummary().value(),
        game.getCoverUrl().value(),
        game.getPlatforms(),
        game.getGameType(),
        game.getGameStatus(),
        game.getParentGameId(),
        game.getGenres(),
        game.getGameModes(),
        game.getPlayerPerspectives(),
        game.getKeywords(),
        game.getInvolvedCompanies(),
        game.getAlternativeNames(),
        game.getFranchises(),
        game.getThemes(),
        game.getExternalGames(),
        game.getMultiplayerModeIds(),
        game.getDlcs(),
        game.getExpandedGames(),
        game.getExpansionIds(),
        game.getRemakeIds(),
        game.getRemasterIds(),
        game.getSimilarGames(),
        game.getScreenshots(),
        game.getVideos());
  }
}
