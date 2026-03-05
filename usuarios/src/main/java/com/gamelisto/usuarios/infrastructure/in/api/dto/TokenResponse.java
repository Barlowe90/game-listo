package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.TokenDTO;
import java.time.Instant;

// Token con fecha de expiración
public record TokenResponse(String token, Instant expiresAt) {

  public static TokenResponse from(TokenDTO tokenDTO) {
    return new TokenResponse(tokenDTO.token(), tokenDTO.expiresAt());
  }
}
