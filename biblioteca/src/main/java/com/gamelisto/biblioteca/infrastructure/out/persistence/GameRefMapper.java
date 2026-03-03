package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.GameRef;
import org.springframework.stereotype.Component;

@Component
public class GameRefMapper {

  public GameRefEntity toEntity(GameRef gameRef) {
    GameRefEntity entity = new GameRefEntity();
    entity.setId(gameRef.getId());
    entity.setNombre(gameRef.getNombre());
    entity.setCover(gameRef.getCover());
    return entity;
  }

  public GameRef toDomain(GameRefEntity entity) {
    return GameRef.reconstitute(entity.getId(), entity.getNombre(), entity.getCover());
  }
}
