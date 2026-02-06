package com.gamelist.catalogo_service.domain.catalog;

import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;

import java.util.Objects;

/**
 * Value Object que representa el nombre completo de una plataforma. Sincronizado desde IGDB.
 * Ejemplo: "PlayStation 5", "Xbox Series X/S", "Nintendo Switch"
 */
public final class PlatformName {
  private static final int MAX_LENGTH = 100;
  private final String value;

  private PlatformName(String value) {
    if (value == null || value.isBlank()) {
      throw new InvalidGameDataException("El nombre de plataforma no puede estar vacío");
    }
    if (value.length() > MAX_LENGTH) {
      throw new InvalidGameDataException(
          "El nombre de plataforma excede los " + MAX_LENGTH + " caracteres");
    }
    this.value = value.trim();
  }

  public static PlatformName of(String value) {
    return new PlatformName(value);
  }

  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PlatformName that = (PlatformName) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "PlatformName{'" + value + "'}";
  }
}
