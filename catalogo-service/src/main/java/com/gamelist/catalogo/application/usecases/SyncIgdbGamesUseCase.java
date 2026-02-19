package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.commands.SyncIgdbGamesCommand;
import com.gamelist.catalogo.application.dto.results.IgdbGameDTO;
import com.gamelist.catalogo.application.dto.results.SyncResultDTO;
import com.gamelist.catalogo.domain.repositories.IEventPublisherPort;
import com.gamelist.catalogo.domain.repositories.IIgdbClientPort;
import com.gamelist.catalogo.domain.catalog.PlatformId;
import com.gamelist.catalogo.domain.events.CatalogGameUpserted;
import com.gamelist.catalogo.domain.events.CatalogSyncBatchCompleted;
import com.gamelist.catalogo.domain.game.*;
import com.gamelist.catalogo.domain.repositories.IGameRepository;
import com.gamelist.catalogo.domain.repositories.ISyncStateRepository;
import com.gamelist.catalogo.domain.syncstate.SyncKey;
import com.gamelist.catalogo.domain.syncstate.SyncState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncIgdbGamesUseCase {

  private final IIgdbClientPort igdbClient;
  private final IGameRepository gameRepository;
  private final ISyncStateRepository syncStateRepository;
  private final IEventPublisherPort eventPublisher;

  @Transactional
  public SyncResultDTO execute(SyncIgdbGamesCommand command) {
    log.info("Iniciando sincronización de juegos desde IGDB");

    // 1. Determinar ID de inicio (checkpoint o comando)
    Long afterId = determineStartId(command);
    log.info("Sincronizando juegos después del ID: {}", afterId);

    // 2. Fetch batch de IGDB
    List<IgdbGameDTO> igdbGames = igdbClient.fetchGamesBatch(afterId, command.limit());
    log.info("Obtenidos {} juegos desde IGDB", igdbGames.size());

    if (igdbGames.isEmpty()) {
      log.info("No hay juegos para sincronizar");
      return new SyncResultDTO(0, afterId);
    }

    // 3. Convertir a dominio y hacer upsert
    igdbGames.forEach(
        dto -> {
          Game game = convertToGame(dto);
          gameRepository.save(game);
          eventPublisher.publish(CatalogGameUpserted.of(game.getId().value()));
        });

    // 4. Actualizar checkpoint
    Long maxId = igdbGames.stream().map(IgdbGameDTO::id).max(Long::compareTo).orElse(afterId);

    updateCheckpoint(maxId);
    log.info("Checkpoint actualizado a: {}", maxId);

    // 5. Publicar evento de batch completado
    eventPublisher.publish(CatalogSyncBatchCompleted.of(igdbGames.size(), maxId));

    log.info("Sincronización completada: {} juegos, último ID: {}", igdbGames.size(), maxId);
    return new SyncResultDTO(igdbGames.size(), maxId);
  }

  private Long determineStartId(SyncIgdbGamesCommand command) {
    // Si el comando especifica un ID, usarlo
    if (command.fromId() != null) {
      return command.fromId();
    }

    // Sino, buscar el checkpoint guardado
    return syncStateRepository
        .findByKey(SyncKey.LAST_SYNCED_GAME_ID)
        .map(SyncState::getValueAsLong)
        .orElse(0L); // Si no hay checkpoint, empezar desde 0
  }

  private void updateCheckpoint(Long maxId) {
    SyncState syncState =
        syncStateRepository
            .findByKey(SyncKey.LAST_SYNCED_GAME_ID)
            .orElseGet(() -> SyncState.create(SyncKey.LAST_SYNCED_GAME_ID, String.valueOf(maxId)));

    syncState.updateValue(String.valueOf(maxId));
    syncStateRepository.save(syncState);
  }

  private Game convertToGame(IgdbGameDTO dto) {
    GameId id = GameId.of(dto.id());
    GameName name = GameName.of(dto.name());
    Summary summary = dto.summary() != null ? Summary.of(dto.summary()) : Summary.empty();
    CoverUrl coverUrl = dto.coverUrl() != null ? CoverUrl.of(dto.coverUrl()) : CoverUrl.empty();

    // Crear el juego
    Game game = Game.create(id, name, summary, coverUrl);

    // Añadir plataformas si existen
    if (dto.platformIds() != null && !dto.platformIds().isEmpty()) {
      Set<PlatformId> platformIds = new HashSet<>();
      dto.platformIds().forEach(platformIdValue -> platformIds.add(PlatformId.of(platformIdValue)));
      game.setPlatforms(platformIds);
    }

    return game;
  }
}
