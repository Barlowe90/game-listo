package com.gamelisto.usuarios_service.domain.refreshtoken;

import com.gamelisto.usuarios_service.domain.exceptions.AlgoritmoNoEncontradoException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

public final class TokenHash {
  private final String value;

  private TokenHash(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("El hash del token no pueder ser nulo o vacío");
    }
    this.value = value;
  }

  public static TokenHash from(TokenValue tokenValue) {
    if (tokenValue == null) {
      throw new IllegalArgumentException("El tokenValue no puede ser nulo");
    }
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hasBytes = digest.digest(tokenValue.value().getBytes(StandardCharsets.UTF_8));
      String hash = Base64.getUrlEncoder().withoutPadding().encodeToString(hasBytes);
      return new TokenHash(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new AlgoritmoNoEncontradoException("SHA-256 no disponible", e);
    }
  }

  public static TokenHash of(String value) {
    return new TokenHash(value);
  }

  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TokenHash that = (TokenHash) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "TokenHash{" + "value='" + value + '\'' + '}';
  }
}
