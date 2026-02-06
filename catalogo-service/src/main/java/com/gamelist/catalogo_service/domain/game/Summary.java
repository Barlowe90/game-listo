package com.gamelist.catalogo_service.domain.game;

import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;

import java.util.Objects;

public final class Summary {
  private static final int MAX_LENGTH = 1000;
  private final String value;

  private Summary(String value) {
    if (value != null && value.length() > MAX_LENGTH) {
      throw new InvalidGameDataException("El resumen excede los " + MAX_LENGTH + " caracteres");
    }
    // Permitimos null o blank para summary (campo opcional)
    this.value = value != null ? value.trim() : null;
  }

  public static Summary of(String value) {
    return new Summary(value);
  }

  public static Summary empty() {
    return new Summary(null);
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
    Summary summary = (Summary) o;
    return Objects.equals(value, summary.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "Summary{'" + (value != null ? value : "empty") + "'}";
  }
}
