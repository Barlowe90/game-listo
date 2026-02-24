package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.mapper;

import com.gamelisto.biblioteca.domain.gameref.GameRef;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.GameRefEntity;
import org.springframework.stereotype.Component;

@Component
public class GameRefMapper {

  public GameRefEntity toEntity(GameRef gameRef) {
    GameRefEntity entity = new GameRefEntity();
    entity.setId(gameRef.getId());
    entity.setGameRefId(gameRef.getGameRefId());
    entity.setNombre(gameRef.getNombre());
    entity.setCover(gameRef.getCover());
    return entity;
  }

  public GameRef toDomain(GameRefEntity entity) {
    return GameRef.reconstitute(
        entity.getId(), entity.getGameRefId(), entity.getNombre(), entity.getCover());
  }
}
