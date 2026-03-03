package com.gamelist.catalogo.domain;

import java.util.Objects;

public final class PlatformAbbreviation {
  private final String value;

  private PlatformAbbreviation(String value) {
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
