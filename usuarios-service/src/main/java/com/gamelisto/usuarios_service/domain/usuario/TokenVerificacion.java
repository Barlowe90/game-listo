package com.gamelisto.usuarios_service.domain.usuario;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public final class TokenVerificacion {

  private static final int TOKEN_LENGTH = 32;
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  private final String value;

  private TokenVerificacion(String value) {
    this.value = value;
  }

  public static TokenVerificacion generate() {
    byte[] randomBytes = new byte[TOKEN_LENGTH];
    SECURE_RANDOM.nextBytes(randomBytes);
    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    return new TokenVerificacion(token);
  }

  public static TokenVerificacion of(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("El token de verificación no puede ser nulo o vacío");
    }
    return new TokenVerificacion(value);
  }

  public static TokenVerificacion empty() {
    return new TokenVerificacion(null);
  }

  public String value() {
    return value;
  }

  public boolean isEmpty() {
    return value == null || value.isBlank();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TokenVerificacion that = (TokenVerificacion) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "TokenVerificacion{value='[PROTECTED]'}";
  }
}
