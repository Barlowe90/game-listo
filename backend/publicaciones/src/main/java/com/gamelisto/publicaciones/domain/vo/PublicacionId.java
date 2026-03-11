package com.gamelisto.publicaciones.domain.vo;

import com.gamelisto.publicaciones.domain.exceptions.DomainException;

import java.util.Objects;
import java.util.UUID;

public final class PublicacionId {
  private final UUID value;

  private PublicacionId(UUID value) {
    if (value == null) throw new DomainException("PublicacionId no puede ser nulo");
    this.value = value;
  }

  public static PublicacionId of(UUID value) {
    return new PublicacionId(value);
  }

  public UUID value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PublicacionId that = (PublicacionId) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "PublicacionId{" + value + '}';
  }
}
