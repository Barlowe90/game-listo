package com.gamelisto.usuarios_service.domain.refreshtoken;

import java.util.Objects;
import java.util.UUID;

public class Jti {
  private final String value;

  private Jti(String value) {
    this.value = value;
  }

  public static Jti generate() {
    return new Jti(UUID.randomUUID().toString());
  }

  public static Jti of(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("El jti no puede ser nulo o vacío");
    }
    // Validar formato UUID
    try {
      UUID.fromString(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("El jti debe ser un UUID válido: " + value);
    }
    return new Jti(value);
  }

  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Jti that = (Jti) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "Jti{value='" + value + "'}"; // No es secreto, puede mostrarse
  }
}
