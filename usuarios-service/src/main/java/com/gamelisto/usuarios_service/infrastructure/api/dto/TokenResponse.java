package com.gamelisto.usuarios_service.infrastructure.api.dto;

import com.gamelisto.usuarios_service.application.dto.TokenDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Token con fecha de expiración")
public record TokenResponse(
    @Schema(
            description = "Token (JWT para access, UUID para refresh)",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,
    @Schema(
            description = "Fecha de expiración del token (ISO 8601)",
            example = "2026-01-30T18:15:00Z")
        Instant expiresAt) {

  public static TokenResponse from(TokenDTO tokenDTO) {
    return new TokenResponse(tokenDTO.token(), tokenDTO.expiresAt());
  }
}
