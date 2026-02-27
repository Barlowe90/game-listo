package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.GameEstado;
import org.springframework.stereotype.Component;

@Component
public class GameEstadoMapper {
  public GameEstadoEntity toEntity(GameEstado gameEstado) {

    GameEstadoEntity entity = new GameEstadoEntity();
    entity.setId(gameEstado.getId());
    entity.setEstado(gameEstado.getEstado());
    entity.setRating(gameEstado.getRating());

    return entity;
  }

  public GameEstado toDomain(GameEstadoEntity entity) {
    return GameEstado.reconstitute(
        entity.getId(),
        entity.getUsuarioRef().getId(),
        entity.getGameRef().getId(),
        entity.getEstado(),
        entity.getRating());
  }
}
