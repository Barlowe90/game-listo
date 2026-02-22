package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.mapper;

import com.gamelisto.biblioteca.domain.gameEstado.GameEstado;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.GameEstadoEntity;
import org.springframework.stereotype.Component;

@Component
public class GameEstadoMapper {
  public GameEstadoEntity toEntity(GameEstado gameEstado) {
    GameEstadoEntity entity = new GameEstadoEntity();
    entity.setId(gameEstado.getId());
    entity.setUsuarioRefId(gameEstado.getUsuarioRefId());
    entity.setGameRefId(gameEstado.getGameRefId());
    entity.setEstado(gameEstado.getEstado());
    entity.setRating(gameEstado.getRating());
    return entity;
  }

  public GameEstado toDomain(GameEstadoEntity entity) {
    return GameEstado.reconstitute(
        entity.getId(),
        entity.getUsuarioRefId(),
        entity.getGameRefId(),
        entity.getEstado(),
        entity.getRating());
  }
}
