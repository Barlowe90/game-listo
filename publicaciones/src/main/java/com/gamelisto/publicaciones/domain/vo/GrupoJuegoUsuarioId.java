package com.gamelisto.publicaciones.domain.vo;

import com.gamelisto.publicaciones.domain.exceptions.DomainException;

import java.util.Objects;
import java.util.UUID;

public final class GrupoJuegoUsuarioId {
  private final UUID value;

  private GrupoJuegoUsuarioId(UUID value) {
    if (value == null) throw new DomainException("GrupoJuegoUsuarioId no puede ser nulo");
    this.value = value;
  }

  public static GrupoJuegoUsuarioId of(UUID value) {
    return new GrupoJuegoUsuarioId(value);
  }

  public UUID value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GrupoJuegoUsuarioId that = (GrupoJuegoUsuarioId) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "GrupoJuegoUsuarioId{" + value + '}';
  }
}
