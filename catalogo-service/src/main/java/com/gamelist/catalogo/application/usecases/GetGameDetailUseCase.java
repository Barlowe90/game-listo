package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.queries.GetGameDetailQuery;
import com.gamelist.catalogo.application.dto.results.GameDetailDTO;
import com.gamelist.catalogo.application.dto.results.GameDTO;
import com.gamelist.catalogo.application.dto.results.PlatformDTO;
import com.gamelist.catalogo.application.exceptions.ApplicationException;
import com.gamelist.catalogo.domain.catalog.Platform;
import com.gamelist.catalogo.domain.game.Game;
import com.gamelist.catalogo.domain.game.GameId;
import com.gamelist.catalogo.domain.gamedetail.GameDetail;
import com.gamelist.catalogo.domain.repositories.IGameDetailRepository;
import com.gamelist.catalogo.domain.repositories.IGameRepository;
import com.gamelist.catalogo.domain.repositories.IPlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetGameDetailUseCase {

  private final IGameRepository gameRepository;
  private final IGameDetailRepository gameDetailRepository;
  private final IPlatformRepository platformRepository;

  @Transactional(readOnly = true)
  public GameDetailDTO execute(GetGameDetailQuery query) {
    log.info("Obteniendo detalle del juego ID: {}", query.gameId());

    // 1. Verificar que el juego existe
    GameId gameId = GameId.of(query.gameId());
    gameRepository
        .findById(gameId)
        .orElseThrow(
            () -> new ApplicationException("Juego no encontrado con ID: " + query.gameId()));

    // 2. Obtener detalle multimedia (puede no existir)
    GameDetail gameDetail =
        gameDetailRepository.findByGameId(gameId).orElse(GameDetail.empty(gameId));

    // 3. Convertir a DTO
    return convertToDTO(gameDetail);
  }

  /** Alternativamente, retorna Game + GameDetail combinados */
  @Transactional(readOnly = true)
  public GameWithDetailDTO executeComplete(GetGameDetailQuery query) {
    log.info("Obteniendo juego completo con detalle ID: {}", query.gameId());

    GameId gameId = GameId.of(query.gameId());

    // 1. Obtener el juego básico
    Game game =
        gameRepository
            .findById(gameId)
            .orElseThrow(
                () -> new ApplicationException("Juego no encontrado con ID: " + query.gameId()));

    // 2. Obtener plataformas
    Set<Platform> platforms =
        game.getPlatformIds().stream()
            .map(platformId -> platformRepository.findById(platformId).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    // 3. Obtener detalle multimedia
    GameDetail gameDetail =
        gameDetailRepository.findByGameId(gameId).orElse(GameDetail.empty(gameId));

    // 4. Convertir a DTOs
    GameDTO gameDTO = convertGameToDTO(game, platforms);
    GameDetailDTO detailDTO = convertToDTO(gameDetail);

    return new GameWithDetailDTO(gameDTO, detailDTO);
  }

  private GameDetailDTO convertToDTO(GameDetail gameDetail) {
    var screenshots =
        gameDetail.getScreenshots().stream()
            .map(s -> new GameDetailDTO.ScreenshotDTO(s.url(), s.width(), s.height()))
            .toList();

    var videos =
        gameDetail.getVideos().stream()
            .map(v -> new GameDetailDTO.VideoDTO(v.videoId(), v.url()))
            .toList();

    return new GameDetailDTO(gameDetail.getGameId().value(), screenshots, videos);
  }

  private GameDTO convertGameToDTO(Game game, Set<Platform> platforms) {
    Set<PlatformDTO> platformDTOs =
        platforms.stream()
            .map(
                p ->
                    new PlatformDTO(
                        p.getId().value(), p.getName().value(), p.getAbbreviation().value()))
            .collect(Collectors.toSet());

    return new GameDTO(
        game.getId().value(),
        game.getName().value(),
        game.getSummary().value(),
        game.getCoverUrl().value(),
        platformDTOs);
  }

  /** DTO que combina Game + GameDetail */
  public record GameWithDetailDTO(GameDTO game, GameDetailDTO detail) {}
}
