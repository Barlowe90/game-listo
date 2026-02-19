package com.gamelist.catalogo_service.domain.game;

import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;

public final class GameId {

  private final Long value;

  private GameId(Long value) {
    if (value == null) {
      throw new InvalidGameDataException("El ID del juego no puede ser nulo");
    }
    if (value <= 0) {
      throw new InvalidGameDataException("El ID del juego debe ser un número positivo");
    }
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
    return value.equals(gameId.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return "GameId{" + value + '}';
  }
}
