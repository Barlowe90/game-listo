package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.mapper;

import com.gamelisto.biblioteca.domain.gameestado.GameEstado;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.GameEstadoEntity;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.UsuarioRefEntity;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.GameRefEntity;
import org.springframework.stereotype.Component;

@Component
public class GameEstadoMapper {
  public GameEstadoEntity toEntity(GameEstado gameEstado) {
    GameEstadoEntity entity = new GameEstadoEntity();
    entity.setId(gameEstado.getId());

    UsuarioRefEntity usuarioRef = new UsuarioRefEntity();
    usuarioRef.setId(gameEstado.getUsuarioRefId());
    entity.setUsuarioRef(usuarioRef);

    GameRefEntity gameRef = new GameRefEntity();
    gameRef.setId(gameEstado.getGameRefId());
    entity.setGameRef(gameRef);

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
