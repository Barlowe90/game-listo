package com.gamelist.catalogo.infrastructure.in.api;

import com.gamelist.catalogo.application.dto.command.BuscarGamePorIdCommand;
import com.gamelist.catalogo.application.dto.command.BuscarGameDetailPorIdCommand;
import com.gamelist.catalogo.application.dto.out.GameDTO;
import com.gamelist.catalogo.application.dto.out.GameDetailDTO;
import com.gamelist.catalogo.application.dto.out.PlatformDTO;
import com.gamelist.catalogo.application.usecases.BuscarGamePorIdUseCase;
import com.gamelist.catalogo.application.usecases.BuscarGameDetailPorIdUseCase;
import com.gamelist.catalogo.application.usecases.ObtenerTodasLasPlatformasUseCase;
import com.gamelist.catalogo.application.usecases.ObtenerTodosLosJuegosUseCase;
import com.gamelist.catalogo.infrastructure.out.dto.GameDetailResponse;
import com.gamelist.catalogo.infrastructure.out.dto.GameResponse;
import com.gamelist.catalogo.infrastructure.out.dto.PlatformResponse;
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

  private final BuscarGameDetailPorIdUseCase getGameDetailUseCase;
  private final ObtenerTodosLosJuegosUseCase obtenerTodosLosJuegosUseCase;
  private final BuscarGamePorIdUseCase getGameByIdUseCase;
  private final ObtenerTodasLasPlatformasUseCase obtenerTodasLasPlatformasUseCase;

  @GetMapping("/games")
  public ResponseEntity<List<GameResponse>> getAllGames(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    logger.info("Obteniendo listado de juegos (page: {}, size: {})", page, size);

    List<GameDTO> gamesDTO = obtenerTodosLosJuegosUseCase.execute();
    List<GameResponse> responses = gamesDTO.stream().map(GameResponse::from).toList();

    logger.info("Retornando {} juegos", responses.size());
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/games/{id}")
  public ResponseEntity<GameResponse> getGameById(@PathVariable Long id) {
    logger.info("Obteniendo juego ID: {} desde PostgreSQL", id);

    BuscarGamePorIdCommand query = new BuscarGamePorIdCommand(id);
    GameDTO dto = getGameByIdUseCase.execute(query);

    return ResponseEntity.ok(GameResponse.from(dto));
  }

  @GetMapping("/games/{id}/detail")
  public ResponseEntity<GameDetailResponse> getGameDetailOnly(@PathVariable Long id) {
    logger.info("Obteniendo Game Detail (MongoDB) del juego ID: {}", id);

    BuscarGameDetailPorIdCommand query = new BuscarGameDetailPorIdCommand(id);
    GameDetailDTO detailDTO = getGameDetailUseCase.execute(query);

    return ResponseEntity.ok(GameDetailResponse.from(detailDTO));
  }

  @GetMapping("/platforms")
  public ResponseEntity<List<PlatformResponse>> getAllPlatforms() {
    logger.info("Obteniendo listado de plataformas");

    List<PlatformDTO> platforms = obtenerTodasLasPlatformasUseCase.execute();
    List<PlatformResponse> responses = platforms.stream().map(PlatformResponse::from).toList();

    return ResponseEntity.ok(responses);
  }
}
