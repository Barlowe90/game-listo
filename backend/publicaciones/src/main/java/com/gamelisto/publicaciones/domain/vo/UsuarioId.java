package com.gamelisto.publicaciones.domain.vo;

import com.gamelisto.publicaciones.domain.exceptions.DomainException;

import java.util.Objects;
import java.util.UUID;

public final class UsuarioId {
  private final UUID value;

  private UsuarioId(UUID value) {
    if (value == null) throw new DomainException("UsuarioId no puede ser nulo");
    this.value = value;
  }

  public static UsuarioId of(UUID value) {
    return new UsuarioId(value);
  }

  public static UsuarioId fromString(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new DomainException("El id de usuario no puede ser nulo o vacío");
    }
    try {
      return new UsuarioId(UUID.fromString(value));
    } catch (DomainException e) {
      throw new DomainException("Formato de UUID inválido: " + value, e);
    }
  }

  public UUID value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UsuarioId usuarioId = (UsuarioId) o;
    return Objects.equals(value, usuarioId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "UsuarioId{" + value + '}';
  }
}
