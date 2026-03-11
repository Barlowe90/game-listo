package com.gamelisto.publicaciones.domain.vo;

import com.gamelisto.publicaciones.domain.exceptions.DomainException;

import java.util.Objects;

public final class GameId {
  private final Long value;

  private GameId(Long value) {
    if (value == null) throw new DomainException("GameId no puede ser nulo");
    if (value <= 0) throw new DomainException("GameId debe ser positivo");
    this.value = value;
  }

  public static GameId of(Long value) {
    return new GameId(value);
  }

  public Long value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GameId gameId = (GameId) o;
    return Objects.equals(value, gameId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "GameId{" + value + '}';
  }
}
