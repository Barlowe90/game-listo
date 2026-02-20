package com.gamelist.catalogo.domain.game;

import com.gamelist.catalogo.domain.exceptions.DomainException;
import java.util.Objects;

public final class Summary {
  private final String value;

  private Summary(String value) {
    if (value != null) {
      String trimmed = value.trim();
      if (trimmed.length() > 1000) {
        throw new DomainException("El resumen del juego excede los 1000 caracteres permitidos");
      }
      this.value = trimmed;
    } else {
      this.value = null;
    }
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
