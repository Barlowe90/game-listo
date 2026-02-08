package com.gamelist.catalogo_service.domain.catalog;

import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;

import java.util.Objects;

public final class PlatformAbbreviation {
  private static final int MAX_LENGTH = 20;
  private final String value;

  private PlatformAbbreviation(String value) {
    // Puede ser null en IGDB (algunas plataformas no tienen abreviación)
    if (value != null && value.length() > MAX_LENGTH) {
      throw new InvalidGameDataException("La abreviación excede los " + MAX_LENGTH + " caracteres");
    }
    this.value = value != null ? value.trim() : null;
  }

  public static PlatformAbbreviation of(String value) {
    return new PlatformAbbreviation(value);
  }

  public static PlatformAbbreviation empty() {
    return new PlatformAbbreviation(null);
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
    PlatformAbbreviation that = (PlatformAbbreviation) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "PlatformAbbreviation{'" + (value != null ? value : "empty") + "'}";
  }
}
