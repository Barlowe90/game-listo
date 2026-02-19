package com.gamelist.catalogo.domain.game;

import com.gamelist.catalogo.domain.exceptions.DomainException;
import java.util.Objects;

public final class GameName {
  private static final int MAX_LENGTH = 200;
  private final String value;

  private GameName(String value) {
    if (value == null || value.isBlank()) {
      throw new DomainException("El nombre del juego no puede estar vacío");
    }
    if (value.length() > MAX_LENGTH) {
      throw new DomainException("El nombre excede los " + MAX_LENGTH + " caracteres");
    }
    this.value = value.trim();
  }

  public static GameName of(String value) {
    return new GameName(value);
  }

  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GameName gameName = (GameName) o;
    return Objects.equals(value, gameName.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "GameName{'" + value + "'}";
  }
}
