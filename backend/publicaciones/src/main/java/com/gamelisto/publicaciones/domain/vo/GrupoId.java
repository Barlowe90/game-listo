package com.gamelisto.publicaciones.domain.vo;

import com.gamelisto.publicaciones.domain.exceptions.DomainException;

import java.util.Objects;
import java.util.UUID;

public final class GrupoId {
  private final UUID value;

  private GrupoId(UUID value) {
    if (value == null) throw new DomainException("GrupoId no puede ser nulo");
    this.value = value;
  }

  public static GrupoId of(UUID value) {
    return new GrupoId(value);
  }

  public UUID value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GrupoId grupoId = (GrupoId) o;
    return Objects.equals(value, grupoId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "GrupoId{" + value + '}';
  }
}
