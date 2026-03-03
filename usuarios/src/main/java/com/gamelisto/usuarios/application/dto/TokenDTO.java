package com.gamelisto.usuarios.application.dto;

import java.time.Instant;

public record TokenDTO(String token, Instant expiresAt) {

  public TokenDTO {
    if (token == null || token.isBlank()) {
      throw new IllegalArgumentException("El token no puede estar vacío");
    }
    if (expiresAt == null) {
      throw new IllegalArgumentException("La fecha de expiración no puede ser nula");
    }
  }
}
