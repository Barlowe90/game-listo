package com.gamelist.catalogo_service.infrastructure.persistence.postgres.mapper;

import com.gamelist.catalogo_service.domain.catalog.PlatformId;
import com.gamelist.catalogo_service.domain.game.*;
import com.gamelist.catalogo_service.infrastructure.persistence.postgres.entity.GameEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GameMapper {

  public GameEntity toEntity(Game game) {
    if (game == null) {
      return null;
    }

    GameEntity entity = new GameEntity();
    entity.setId(game.getId().value());
    entity.setName(game.getName().value());
    entity.setSummary(
        game.getSummary() != null && !game.getSummary().isEmpty()
            ? game.getSummary().value()
            : null);
    entity.setCoverUrl(
        game.getCoverUrl() != null && !game.getCoverUrl().isEmpty()
            ? game.getCoverUrl().value()
            : null);
    entity.setCreatedAt(game.getCreatedAt());
    entity.setUpdatedAt(game.getUpdatedAt());

    // Las plataformas se manejan a nivel de repositorio para evitar lazy loading issues

    return entity;
  }

  public Game toDomain(GameEntity entity) {
    if (entity == null) {
      return null;
    }

    GameId id = GameId.of(entity.getId());
    GameName name = GameName.of(entity.getName());
    Summary summary =
        entity.getSummary() != null ? Summary.of(entity.getSummary()) : Summary.empty();
    CoverUrl coverUrl =
        entity.getCoverUrl() != null ? CoverUrl.of(entity.getCoverUrl()) : CoverUrl.empty();

    // Convertir plataformas (Set<PlatformEntity> -> Set<PlatformId>)
    Set<PlatformId> platformIds =
        entity.getPlatforms().stream()
            .map(platformEntity -> PlatformId.of(platformEntity.getId()))
            .collect(Collectors.toSet());

    return Game.reconstitute(
        id, name, summary, coverUrl, platformIds, entity.getCreatedAt(), entity.getUpdatedAt());
  }

  public void updateEntity(Game game, GameEntity entity) {
    entity.setName(game.getName().value());
    entity.setSummary(
        game.getSummary() != null && !game.getSummary().isEmpty()
            ? game.getSummary().value()
            : null);
    entity.setCoverUrl(
        game.getCoverUrl() != null && !game.getCoverUrl().isEmpty()
            ? game.getCoverUrl().value()
            : null);
    entity.setUpdatedAt(game.getUpdatedAt());
  }
}
