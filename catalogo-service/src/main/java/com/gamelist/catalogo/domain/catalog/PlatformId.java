package com.gamelist.catalogo_service.domain.catalog;

import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;

import java.util.Objects;

public final class PlatformId {
  private final Long value;

  private PlatformId(Long value) {
    if (value == null) {
      throw new InvalidGameDataException("El ID de plataforma no puede ser nulo");
    }
    if (value <= 0) {
      throw new InvalidGameDataException("El ID de plataforma debe ser un número positivo");
    }
    this.value = value;
  }

  public static PlatformId of(Long value) {
    return new PlatformId(value);
  }

  public Long value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PlatformId that = (PlatformId) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "PlatformId{" + value + '}';
  }
}
