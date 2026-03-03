package com.gamelist.catalogo.domain;

import com.gamelist.catalogo.domain.exceptions.DomainException;
import java.util.Objects;

public final class PlatformName {
  private static final int MAX_LENGTH = 100;
  private final String value;

  private PlatformName(String value) {
    if (value == null || value.isBlank()) {
      throw new DomainException("El nombre de plataforma no puede estar vacío");
    }
    if (value.length() > MAX_LENGTH) {
      throw new DomainException("El nombre de plataforma excede los " + MAX_LENGTH + " caracteres");
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
