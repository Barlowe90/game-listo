package com.gamelisto.biblioteca.domain;

import java.util.Objects;

import com.gamelisto.biblioteca.domain.exceptions.DomainException;
import com.gamelisto.biblioteca.domain.GameEstadoId;
import com.gamelisto.biblioteca.domain.UsuarioId;
import com.gamelisto.biblioteca.domain.GameId;
import com.gamelisto.biblioteca.domain.Rating;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GameEstado {
  private final GameEstadoId id;
  private final UsuarioId usuarioRefId;
  private final GameId gameRefId;
  private final Estado estado;
  private final Rating rating;

  private GameEstado(GameEstadoId id, UsuarioId usuarioRefId, GameId gameRefId, Estado estado, Rating rating) {
    if (usuarioRefId == null)
      throw new DomainException("usuarioRefId no puede ser nulo");
    if (gameRefId == null)
      throw new DomainException("gameRefId no puede ser nulo");
    if (estado == null)
      throw new DomainException("estado no puede ser nulo");
    if (rating == null)
      throw new DomainException("rating no puede ser nulo");

    this.id = id;
    this.usuarioRefId = usuarioRefId;
    this.gameRefId = gameRefId;
    this.estado = estado;
    this.rating = rating;
  }

  public static GameEstado create(UsuarioId usuarioRefId, GameId gameRefId, Estado estado, Rating rating) {
    return new GameEstado(GameEstadoId.generate(), usuarioRefId, gameRefId, estado, rating);
  }

  public static GameEstado create(java.util.UUID usuarioRefId, Long gameRefId, Estado estado, double rating) {
    return create(UsuarioId.of(usuarioRefId), GameId.of(gameRefId), estado, Rating.of(rating));
  }

  public static GameEstado reconstitute(
      GameEstadoId id, UsuarioId usuarioRefId, GameId gameRefId, Estado estado, Rating rating) {
    return new GameEstado(id, usuarioRefId, gameRefId, estado, rating);
  }

  public static GameEstado reconstitute(
      GameEstadoId id, java.util.UUID usuarioRefId, Long gameRefId, Estado estado, double rating) {
    return reconstitute(id, UsuarioId.of(usuarioRefId), GameId.of(gameRefId), estado, Rating.of(rating));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    GameEstado that = (GameEstado) o;
    return Objects.equals(id, that.id)
        && Objects.equals(usuarioRefId, that.usuarioRefId)
        && Objects.equals(gameRefId, that.gameRefId)
        && Objects.equals(estado, that.estado)
        && Objects.equals(rating, that.rating);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, usuarioRefId, gameRefId, estado, rating);
  }
}
