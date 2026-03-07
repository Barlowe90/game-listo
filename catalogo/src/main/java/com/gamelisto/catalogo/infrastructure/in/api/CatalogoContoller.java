package com.gamelisto.catalogo.infrastructure.in.api;

import com.gamelisto.catalogo.application.dto.command.BuscarGamePorIdCommand;
import com.gamelisto.catalogo.application.dto.command.BuscarGameDetailPorIdCommand;
import com.gamelisto.catalogo.application.dto.out.GameDTO;
import com.gamelisto.catalogo.application.dto.out.GameDetailDTO;
import com.gamelisto.catalogo.application.dto.out.PlatformDTO;
import com.gamelisto.catalogo.application.usecases.BuscarGameDetailPorIdHandle;
import com.gamelisto.catalogo.application.usecases.BuscarGamePorIdHandle;
import com.gamelisto.catalogo.application.usecases.ObtenerTodasLasPlatformasHandle;
import com.gamelisto.catalogo.application.usecases.ObtenerTodosLosJuegosHandle;
import com.gamelisto.catalogo.infrastructure.out.dto.GameDetailResponse;
import com.gamelisto.catalogo.infrastructure.out.dto.GameResponse;
import com.gamelisto.catalogo.infrastructure.out.dto.PlatformResponse;
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

  private final BuscarGameDetailPorIdHandle getGameDetail;
  private final ObtenerTodosLosJuegosHandle obtenerTodosLosJuegos;
  private final BuscarGamePorIdHandle getGameById;
  private final ObtenerTodasLasPlatformasHandle obtenerTodasLasPlatformas;

  @GetMapping("/games")
  public ResponseEntity<List<GameResponse>> getAllGames(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    logger.info("Obteniendo listado de juegos (page: {}, size: {})", page, size);

    List<GameDTO> gamesDTO = obtenerTodosLosJuegos.execute();
    List<GameResponse> responses = gamesDTO.stream().map(GameResponse::from).toList();

    logger.info("Retornando {} juegos", responses.size());
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/games/{id}")
  public ResponseEntity<GameResponse> getGameById(@PathVariable Long id) {
    logger.info("Obteniendo juego ID: {} desde PostgreSQL", id);

    BuscarGamePorIdCommand query = new BuscarGamePorIdCommand(id);
    GameDTO dto = getGameById.execute(query);

    return ResponseEntity.ok(GameResponse.from(dto));
  }

  @GetMapping("/games/{id}/detail")
  public ResponseEntity<GameDetailResponse> getGameDetailOnly(@PathVariable Long id) {
    logger.info("Obteniendo Game Detail (MongoDB) del juego ID: {}", id);

    BuscarGameDetailPorIdCommand query = new BuscarGameDetailPorIdCommand(id);
    GameDetailDTO detailDTO = getGameDetail.execute(query);

    return ResponseEntity.ok(GameDetailResponse.from(detailDTO));
  }

  @GetMapping("/platforms")
  public ResponseEntity<List<PlatformResponse>> getAllPlatforms() {
    logger.info("Obteniendo listado de plataformas");

    List<PlatformDTO> platforms = obtenerTodasLasPlatformas.execute();
    List<PlatformResponse> responses = platforms.stream().map(PlatformResponse::from).toList();

    return ResponseEntity.ok(responses);
  }
}
