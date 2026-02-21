package com.gamelisto.biblioteca.domain;

import java.util.Objects;
import java.util.UUID;

import com.gamelisto.biblioteca.exceptions.DomainException;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GameEstado {
  private final UUID id;
  private final String usuarioRefId;
  private final Estado estado;
  private final double rating;

  public GameEstado(String usuarioRefId, Estado estado, double rating) {
    validateUsuarioRefId(usuarioRefId);
    validateRating(rating);
    this.id = UUID.randomUUID();
    this.usuarioRefId = usuarioRefId;
    this.estado = Objects.requireNonNull(estado, "estado no puede ser null");
    this.rating = rating;
  }

  // Constructor privado usado para reconstitución desde persistencia
  private GameEstado(UUID id, String usuarioRefId, Estado estado, double rating) {
    this.id = Objects.requireNonNull(id, "id no puede ser null");
    validateUsuarioRefId(usuarioRefId);
    validateRating(rating);
    this.usuarioRefId = usuarioRefId;
    this.estado = Objects.requireNonNull(estado, "estado no puede ser null");
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

  private static void validateUsuarioRefId(String usuarioRefId) {
    if (usuarioRefId == null || usuarioRefId.trim().isEmpty()) {
      throw new DomainException("usuarioRefId no puede ser null o vacío");
    }
  }

  public static GameEstado create(String usuarioRefId, Estado estado, double rating) {
    return new GameEstado(usuarioRefId, estado, rating);
  }

  public static GameEstado reconstitute(
      UUID id, String usuarioRefId, Estado estado, double rating) {
    return new GameEstado(id, usuarioRefId, estado, rating);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GameEstado that = (GameEstado) o;
    return Double.compare(that.rating, rating) == 0
        && Objects.equals(id, that.id)
        && Objects.equals(usuarioRefId, that.usuarioRefId)
        && Objects.equals(estado, that.estado);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, usuarioRefId, estado, rating);
  }
}
