package com.gamelist.catalogo_service.domain.game;

import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;

import java.util.Objects;

public final class CoverUrl {
  private static final int MAX_LENGTH = 500;
  private final String value;

  private CoverUrl(String value) {
    if (value != null && value.length() > MAX_LENGTH) {
      throw new InvalidGameDataException(
          "La URL de portada excede los " + MAX_LENGTH + " caracteres");
    }
    // Permitimos null para cover (algunos juegos pueden no tener portada)
    this.value = value != null ? value.trim() : null;
  }

  public static CoverUrl of(String value) {
    return new CoverUrl(value);
  }

  public static CoverUrl empty() {
    return new CoverUrl(null);
  }

  public String value() {
    return value;
  }

  public boolean isEmpty() {
    return value == null || value.isBlank();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CoverUrl coverUrl = (CoverUrl) o;
    return Objects.equals(value, coverUrl.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "CoverUrl{'" + (value != null ? value : "empty") + "'}";
  }
}
