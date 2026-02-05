package com.gamelisto.usuarios_service.infrastructure.api.dto;

import com.gamelisto.usuarios_service.application.dto.AuthResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de autenticación exitosa con tokens y datos del usuario")
public record AuthResponse(
    @Schema(description = "Access token JWT (15 minutos de vida)") TokenResponse accessToken,
    @Schema(description = "Refresh token UUID (7 días de vida)") TokenResponse refreshToken,
    @Schema(description = "Datos del usuario autenticado") UsuarioResponse usuario) {

  public static AuthResponse from(AuthResponseDTO authResponseDTO) {
    return new AuthResponse(
        TokenResponse.from(authResponseDTO.accessToken()),
        TokenResponse.from(authResponseDTO.refreshToken()),
        UsuarioResponse.from(authResponseDTO.usuario()));
  }
}
