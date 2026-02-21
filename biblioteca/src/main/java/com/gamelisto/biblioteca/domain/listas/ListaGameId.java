package com.gamelisto.biblioteca.domain.listas;

import java.util.UUID;

public final class ListaGameId {

  private final UUID value;

  private ListaGameId(UUID value) {
    if (value == null) {
      throw new IllegalArgumentException("El ID de la lista de juegos no puede ser nulo");
    }
    this.value = value;
  }

  public static ListaGameId of(UUID value) {
    return new ListaGameId(value);
  }

  public static ListaGameId generate() {
    return new ListaGameId(UUID.randomUUID());
  }

  public static ListaGameId fromString(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("El ID de la lista de juegos no puede ser nulo o vacío");
    }
    try {
      return new ListaGameId(UUID.fromString(value));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Formato de UUID inválido: " + value);
    }
  }

  public UUID value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ListaGameId usuarioId = (ListaGameId) o;
    return value.equals(usuarioId.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
