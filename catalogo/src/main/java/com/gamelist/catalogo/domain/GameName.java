package com.gamelist.catalogo.domain;

import com.gamelist.catalogo.domain.exceptions.DomainException;
import java.util.Objects;

public final class GameName {
  private final String value;

  private GameName(String value) {
    if (value == null || value.isBlank()) {
      throw new DomainException("El nombre del juego no puede estar vacío");
    }
    String trimmed = value.trim();
    if (trimmed.length() > 200) {
      throw new DomainException("El nombre del juego excede los 200 caracteres permitidos");
    }
    this.value = trimmed;
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
