package com.gamelisto.biblioteca.domain;

import java.util.Objects;
import java.util.UUID;
import com.gamelisto.biblioteca.domain.exceptions.DomainException;

public final class GameEstadoId {

  private final UUID value;

  private GameEstadoId(UUID value) {
    if (value == null) {
      throw new DomainException("El id de GameEstado no puede ser nulo");
    }
    this.value = value;
  }

  public static GameEstadoId of(UUID value) {
    return new GameEstadoId(value);
  }

  public static GameEstadoId generate() {
    return new GameEstadoId(UUID.randomUUID());
  }

  public static GameEstadoId fromString(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new DomainException("El id de GameEstado no puede ser nulo o vacío");
    }
    try {
      return new GameEstadoId(UUID.fromString(value));
    } catch (IllegalArgumentException e) {
      throw new DomainException("Formato de UUID inválido: " + value, e);
    }
  }

  public UUID value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GameEstadoId that = (GameEstadoId) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
