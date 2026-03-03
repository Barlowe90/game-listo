package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.in.IgdbGameDTO;
import com.gamelist.catalogo.application.dto.out.SyncResultDTO;
import com.gamelist.catalogo.domain.events.GameCreado;
import com.gamelist.catalogo.domain.game.Game;
import com.gamelist.catalogo.domain.gamedetail.GameDetail;
import com.gamelist.catalogo.domain.repositories.IGameDetailRepository;
import com.gamelist.catalogo.domain.repositories.IGamePublisher;
import com.gamelist.catalogo.domain.repositories.RepositorioGame;
import com.gamelist.catalogo.domain.repositories.IIgdbClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Accede a IGDB para persistir en bbdd la info de los videojuegos */
@Service
@RequiredArgsConstructor
public class SyncGamesFromIGDBUseCase {

  private static final String ROUTING_KEY_SUFFIX = "gameEstado.creado";
  private static final Logger logger = LoggerFactory.getLogger(SyncGamesFromIGDBUseCase.class);

  private final IIgdbClientPort igdbClient;
  private final RepositorioGame gameRepository;
  private final IGameDetailRepository gameDetailRepository;
  private final IGamePublisher eventosPublisher;

  @Transactional
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

        enviarColaGameCreado(game);

        GameDetail gameDetail =
            GameDetail.create(
                game.getId(),
                game.getAlternativeNames(),
                game.getCoverUrl().isEmpty() ? null : game.getCoverUrl().value(),
                game.getScreenshots(),
                game.getVideos());
        gameDetailRepository.save(gameDetail); // mongoDB

        logger.debug(
            "Juego guardado en PostgreSQL y MongoDB: ID={}, Name={}", dto.id(), dto.name());
      } catch (Exception e) {
        logger.error(
            "Error al guardar juego: ID={}, Name={}, Error={}",
            dto.id(),
            dto.name(),
            e.getMessage());
      }
    }

    Long maxId = igdbGames.stream().map(IgdbGameDTO::id).max(Long::compareTo).orElse(afterId);
    logger.info("Sincronización completada: {} juegos, último ID: {}", igdbGames.size(), maxId);
    return new SyncResultDTO(igdbGames.size(), maxId);
  }

  private void enviarColaGameCreado(Game game) {
    GameCreado evento =
        GameCreado.of(
            game.getId().toString(), game.getName().toString(), game.getCoverUrl().toString());
    eventosPublisher.publish(ROUTING_KEY_SUFFIX, evento);
  }
}
