package com.gamelisto.biblioteca.domain.gameEstado;

import java.util.Objects;
import java.util.UUID;

import com.gamelisto.biblioteca.domain.exceptions.DomainException;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GameEstado {
  private final UUID id;
  private final UUID usuarioRefId;
  private final UUID gameRefId;
  private final Estado estado;
  private final double rating;

  public GameEstado(UUID usuarioRefId, UUID gameRefId, Estado estado, double rating) {
    validateRating(rating);
    this.id = UUID.randomUUID();
    this.usuarioRefId = usuarioRefId;
    this.gameRefId = gameRefId;
    this.estado = estado;
    this.rating = rating;
  }

  private GameEstado(UUID id, UUID usuarioRefId, UUID gameRefId, Estado estado, double rating) {
    this.id = id;
    validateRating(rating);
    this.usuarioRefId = usuarioRefId;
    this.gameRefId = gameRefId;
    this.estado = estado;
    this.rating = rating;
  }

  private static void validateRating(double rating) {
    if (!Double.isFinite(rating)) {
      throw new DomainException("El rating debe ser un número finito entre 0.0 y 5.0");
    }

    if (rating < 0.0 || rating > 5.0) {
      throw new DomainException("El rating debe estar entre 0.0 y 5.0");
    }
  }

  public static GameEstado create(UUID usuarioRefId, UUID gameRefId, Estado estado, double rating) {
    return new GameEstado(usuarioRefId, gameRefId, estado, rating);
  }

  public static GameEstado reconstitute(
      UUID id, UUID usuarioRefId, UUID gameRefId, Estado estado, double rating) {
    return new GameEstado(id, usuarioRefId, gameRefId, estado, rating);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GameEstado that = (GameEstado) o;
    return Double.compare(that.rating, rating) == 0
        && Objects.equals(id, that.id)
        && Objects.equals(usuarioRefId, that.usuarioRefId)
        && Objects.equals(gameRefId, that.gameRefId)
        && Objects.equals(estado, that.estado);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, usuarioRefId, gameRefId, estado, rating);
  }
}
