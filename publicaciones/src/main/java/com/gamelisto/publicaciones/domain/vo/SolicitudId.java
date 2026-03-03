package com.gamelisto.publicaciones.domain.vo;

import com.gamelisto.publicaciones.domain.exceptions.DomainException;

import java.util.Objects;
import java.util.UUID;

public final class SolicitudId {
  private final UUID value;

  private SolicitudId(UUID value) {
    if (value == null) throw new DomainException("SolicitudId no puede ser nulo");
    this.value = value;
  }

  public static SolicitudId of(UUID value) {
    return new SolicitudId(value);
  }

  public UUID value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SolicitudId that = (SolicitudId) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "SolicitudId{" + value + '}';
  }
}
