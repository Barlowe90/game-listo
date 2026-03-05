package com.gamelisto.catalogo.domain;

import com.gamelisto.catalogo.domain.exceptions.DomainException;
import java.util.Objects;

public final class CoverUrl {
  private final String value;

  private CoverUrl(String value) {
    if (value != null) {
      String trimmed = value.trim();
      if (trimmed.length() > 500) {
        throw new DomainException("La URL de portada excede los 500 caracteres permitidos");
      }
      this.value = trimmed;
    } else {
      this.value = null;
    }
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
