package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.in.IgdbGameDTO;
import com.gamelist.catalogo.application.dto.out.SyncResultDTO;
import com.gamelist.catalogo.domain.events.GameCreado;
import com.gamelist.catalogo.domain.Game;
import com.gamelist.catalogo.domain.GameDetail;
import com.gamelist.catalogo.domain.GameDetailRepositorio;
import com.gamelist.catalogo.domain.GamePublisherRepositorio;
import com.gamelist.catalogo.domain.GameRepositorio;
import com.gamelist.catalogo.domain.IgdbClientPortRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
  public SyncResultDTO execute(int limit) {
    logger.info("Iniciando sincronización de juegos desde IGDB");

    // 1. Determinar desde qué ID continuar (siempre desde el último guardado)
    Long afterId = gameRepository.findMaxId();
    logger.info("Sincronizando juegos después del ID: {}", afterId);

    // 2. Fetch batch IGDB
    List<IgdbGameDTO> igdbGames = igdbClient.fetchGamesBatch(afterId, limit);
    logger.info("Obtenidos {} juegos desde IGDB", igdbGames.size());

    if (igdbGames.isEmpty()) {
      logger.info("No hay juegos para sincronizar");
      return new SyncResultDTO(0, afterId);
    }

    // 3. Convertir y persistir
    for (IgdbGameDTO dto : igdbGames) {
      try {
        Game game = dto.toDomain();
        gameRepository.save(game); // postgreSQL

        GameDetail gameDetail = GameDetail.create(game.getId(), dto.screenshots(), dto.videos());
        gameDetailRepository.save(gameDetail); // mongoDB

        publicarAfterCommit(() -> publicarEventoGameCreado(game));

        logger.debug(
            "Juego guardado en PostgreSQL y MongoDB: ID={}, Name={}", dto.id(), dto.name());
      } catch (Exception e) {
        logger.error("Error al guardar juego: ID={}, Name={}, Error=", dto.id(), dto.name(), e);
      }
    }

    Long maxId = igdbGames.stream().map(IgdbGameDTO::id).max(Long::compareTo).orElse(afterId);
    logger.info("Sincronización completada: {} juegos, último ID: {}", igdbGames.size(), maxId);
    return new SyncResultDTO(igdbGames.size(), maxId);
  }

  private void publicarEventoGameCreado(Game game) {
    GameCreado evento =
        GameCreado.of(
            game.getId() != null ? game.getId().toString() : null,
            game.getName() != null ? game.getName().toString() : null,
            game.getSummary() != null ? game.getSummary().toString() : null,
            game.getCoverUrl() != null ? game.getCoverUrl().toString() : null,
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

  private static void publicarAfterCommit(Runnable action) {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              action.run();
            }
          });
    } else {
      action.run();
    }
  }
}
