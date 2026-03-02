package com.gamelisto.publicaciones.domain.vo;

import com.gamelisto.publicaciones.domain.exceptions.DomainException;

import java.util.Objects;
import java.util.UUID;

public final class PeticionId {
  private final UUID value;

  private PeticionId(UUID value) {
    if (value == null) throw new DomainException("PeticionId no puede ser nulo");
    this.value = value;
  }

  public static PeticionId of(UUID value) {
    return new PeticionId(value);
  }

  public UUID value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PeticionId that = (PeticionId) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "PeticionId{" + value + '}';
  }
}
