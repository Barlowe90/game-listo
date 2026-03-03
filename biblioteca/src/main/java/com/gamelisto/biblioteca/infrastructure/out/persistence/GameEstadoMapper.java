package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.GameEstado;
import com.gamelisto.biblioteca.domain.GameEstadoId;
import com.gamelisto.biblioteca.domain.UsuarioId;
import com.gamelisto.biblioteca.domain.GameId;
import com.gamelisto.biblioteca.domain.Rating;
import org.springframework.stereotype.Component;

@Component
public class GameEstadoMapper {
  public GameEstadoEntity toEntity(GameEstado gameEstado) {

    GameEstadoEntity entity = new GameEstadoEntity();
    entity.setId(gameEstado.getId().value());
    entity.setEstado(gameEstado.getEstado());
    entity.setRating(gameEstado.getRating().value());

    return entity;
  }

  public GameEstado toDomain(GameEstadoEntity entity) {
    return GameEstado.reconstitute(
        GameEstadoId.of(entity.getId()),
        UsuarioId.of(entity.getUsuarioRef().getId()),
        GameId.of(entity.getGameRef().getId()),
        entity.getEstado(),
        Rating.of(entity.getRating()));
  }
}
