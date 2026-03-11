package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.domain.events.GameCreado;
import com.gamelisto.catalogo.domain.Game;
import com.gamelisto.catalogo.domain.GameDetail;
import com.gamelisto.catalogo.domain.GameDetailRepositorio;
import com.gamelisto.catalogo.domain.GamePublisherRepositorio;
import com.gamelisto.catalogo.domain.GameRepositorio;
import com.gamelisto.catalogo.domain.IgdbClientPortRepositorio;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Accede a IGDB para persistir en bbdd la info de los videojuegos */
@Service
@RequiredArgsConstructor
public class SyncGamesFromIGDBUseCase implements SyncGamesFromIGDBHandle {

  private static final Logger logger = LoggerFactory.getLogger(SyncGamesFromIGDBUseCase.class);

  private final IgdbClientPortRepositorio igdbClient;
  private final GameRepositorio gameRepository;
  private final GameDetailRepositorio gameDetailRepository;
  private final GamePublisherRepositorio eventosPublisher;

  @Override
  public SyncResultResult execute(int limit) {
    Long afterId = determinarQueIdContinuar();

    List<IgdbGameDTO> igdbGames = fetchBatchIGDB(limit, afterId);

    SyncResultResult afterId1 = comprobarSiSeHanSincronizadoJuegos(igdbGames, afterId);
    if (afterId1 != null) return afterId1;

    convertirAndPersistir(igdbGames);

    Long maxId = obtenerUltimoIdSincronizado(igdbGames, afterId);
    logger.info("Sincronización completada: {} juegos, último ID: {}", igdbGames.size(), maxId);
    return new SyncResultResult(igdbGames.size(), maxId);
  }

  private static @Nullable SyncResultResult comprobarSiSeHanSincronizadoJuegos(
      List<IgdbGameDTO> igdbGames, Long afterId) {
    if (igdbGames.isEmpty()) {
      logger.info("No hay juegos para sincronizar");
      return new SyncResultResult(0, afterId);
    }
    return null;
  }

  private static Long obtenerUltimoIdSincronizado(List<IgdbGameDTO> igdbGames, Long afterId) {
    return igdbGames.stream().map(IgdbGameDTO::id).max(Long::compareTo).orElse(afterId);
  }

  private void convertirAndPersistir(List<IgdbGameDTO> igdbGames) {
    for (IgdbGameDTO dto : igdbGames) {
      try {
        Game game = dto.toDomain();
        gameRepository.save(game); // postgreSQL

        GameDetail gameDetail = GameDetail.create(game.getId(), dto.screenshots(), dto.videos());
        gameDetailRepository.save(gameDetail); // mongoDB

        publicarEventoGameCreado(game);

      } catch (Exception e) {
        logger.error("Error al guardar juego: ID={}, Name={}, Error=", dto.id(), dto.name(), e);
      }
    }
  }

  private List<IgdbGameDTO> fetchBatchIGDB(int limit, Long afterId) {
    return igdbClient.fetchGamesBatch(afterId, limit);
  }

  private @NonNull Long determinarQueIdContinuar() {
    return gameRepository.findMaxId();
  }

  private void publicarEventoGameCreado(Game game) {
    GameCreado evento =
        GameCreado.of(
            game.getId() != null ? game.getId().value() : null,
            game.getName() != null ? game.getName().value() : null,
            game.getSummary() != null ? game.getSummary().value() : null,
            game.getCoverUrl() != null ? game.getCoverUrl().value() : null,
            game.getPlatforms(),
            game.getGameType(),
            game.getGameStatus(),
            game.getAlternativeNames(),
            game.getDlcs(),
            game.getExpandedGames(),
            game.getExpansionIds(),
            game.getExternalGames(),
            game.getFranchises(),
            game.getGameModes(),
            game.getGenres(),
            game.getInvolvedCompanies(),
            game.getKeywords(),
            game.getMultiplayerModeIds(),
            game.getParentGameId(),
            game.getPlayerPerspectives(),
            game.getRemakeIds(),
            game.getRemasterIds(),
            game.getSimilarGames(),
            game.getThemes());

    eventosPublisher.publicarGameCreado(evento);
  }
}
