package com.gamelisto.usuarios_service.domain.refreshtoken;

import java.util.Objects;
import java.util.UUID;

public final class TokenValue {

  private final String value;

  private TokenValue(String value) {
    this.value = value;
  }

  public static TokenValue generate() {
    return new TokenValue(UUID.randomUUID().toString());
  }

  public static TokenValue of(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("El refresh token no puede ser nulo o vacío");
    }
    // Validar formato UUID
    try {
      UUID.fromString(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("El refresh token debe ser un UUID válido: " + value, e);
    }
    return new TokenValue(value);
  }

  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TokenValue that = (TokenValue) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "TokenValue{value='[PROTECTED]'}"; // No exponer en logs por seguridad
  }
}
