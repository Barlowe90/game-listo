package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.results.IgdbGameDTO;
import com.gamelist.catalogo.application.dto.results.SyncResultDTO;
import com.gamelist.catalogo.domain.game.Game;
import com.gamelist.catalogo.domain.gamedetail.GameDetail;
import com.gamelist.catalogo.domain.events.CatalogGameUpserted;
import com.gamelist.catalogo.domain.events.CatalogSyncBatchCompleted;
import com.gamelist.catalogo.domain.repositories.IGameDetailRepository;
import com.gamelist.catalogo.domain.repositories.IGameRepository;
import com.gamelist.catalogo.domain.repositories.IEventPublisherPort;
import com.gamelist.catalogo.domain.repositories.IIgdbClientPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Accede a IGDB para persistir en bbdd la info de los videojuegos */
@Service
@RequiredArgsConstructor
@Slf4j
public class SyncIgdbGamesUseCase {

  private final IIgdbClientPort igdbClient;
  private final IGameRepository gameRepository;
  private final IGameDetailRepository gameDetailRepository;
  private final IEventPublisherPort eventPublisher;

  @Transactional
  public SyncResultDTO execute(int limit) {
    log.info("Iniciando sincronización de juegos desde IGDB");

    // 1. Determinar desde qué ID continuar (siempre desde el último guardado)
    Long afterId = gameRepository.findMaxId();
    log.info("Sincronizando juegos después del ID: {}", afterId);

    // 2. Fetch batch IGDB
    List<IgdbGameDTO> igdbGames = igdbClient.fetchGamesBatch(afterId, limit);
    log.info("Obtenidos {} juegos desde IGDB", igdbGames.size());

    if (igdbGames.isEmpty()) {
      log.info("No hay juegos para sincronizar");
      return new SyncResultDTO(0, afterId);
    }

    // 3. Convertir y persistir
    for (IgdbGameDTO dto : igdbGames) {
      try {
        Game game = dto.toDomain();
        gameRepository.save(game); // postgreSQL
        GameDetail gameDetail =
            GameDetail.create(
                game.getId(),
                game.getAlternativeNames(),
                game.getCoverUrl().isEmpty() ? null : game.getCoverUrl().value(),
                game.getScreenshots(),
                game.getVideos());
        gameDetailRepository.save(gameDetail); // mongoDB

        eventPublisher.publish(CatalogGameUpserted.of(game.getId().value()));
        log.debug("Juego guardado en PostgreSQL y MongoDB: ID={}, Name={}", dto.id(), dto.name());
      } catch (Exception e) {
        log.error(
            "Error al guardar juego: ID={}, Name={}, Error={}",
            dto.id(),
            dto.name(),
            e.getMessage());
      }
    }

    // 4. Publicar evento de batch completado
    Long maxId = igdbGames.stream().map(IgdbGameDTO::id).max(Long::compareTo).orElse(afterId);
    eventPublisher.publish(CatalogSyncBatchCompleted.of(igdbGames.size(), maxId));

    log.info("Sincronización completada: {} juegos, último ID: {}", igdbGames.size(), maxId);
    return new SyncResultDTO(igdbGames.size(), maxId);
  }
}
