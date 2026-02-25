package com.gamelisto.biblioteca.domain;

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
  private final Long gameRefId;
  private final Estado estado;
  private final double rating;

  private GameEstado(UUID id, UUID usuarioRefId, Long gameRefId, Estado estado, double rating) {
    if (usuarioRefId == null) throw new DomainException("usuarioRefId no puede ser nulo");
    if (gameRefId == null) throw new DomainException("gameRefId no puede ser nulo");
    if (estado == null) throw new DomainException("estado no puede ser nulo");
    validateRating(rating);

    this.id = id;
    this.usuarioRefId = usuarioRefId;
    this.gameRefId = gameRefId;
    this.estado = estado;
    this.rating = rating;
  }

  private static void validateRating(double rating) {
    double scaled = rating * 4.0;

    if (Math.abs(scaled - Math.rint(scaled)) > 1e-9) {
      throw new DomainException("El rating debe ir en incrementos de 0.25");
    }

    if (!Double.isFinite(rating)) {
      throw new DomainException("El rating debe ser un número finito entre 0.0 y 5.0");
    }

    if (rating < 0.0 || rating > 5.0) {
      throw new DomainException("El rating debe estar entre 0.0 y 5.0");
    }
  }

  public static GameEstado create(UUID usuarioRefId, Long gameRefId, Estado estado, double rating) {
    return new GameEstado(UUID.randomUUID(), usuarioRefId, gameRefId, estado, rating);
  }

  public static GameEstado reconstitute(
      UUID id, UUID usuarioRefId, Long gameRefId, Estado estado, double rating) {
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
