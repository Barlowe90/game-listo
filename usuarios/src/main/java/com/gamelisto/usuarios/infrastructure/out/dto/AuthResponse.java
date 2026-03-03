package com.gamelisto.usuarios.infrastructure.out.dto;

import com.gamelisto.usuarios.application.dto.AuthResponseDTO;

// Respuesta de autenticación exitosa con tokens y datos del usuario
public record AuthResponse(
    TokenResponse accessToken, TokenResponse refreshToken, UsuarioResponse usuario) {

  public static AuthResponse from(AuthResponseDTO authResponseDTO) {
    return new AuthResponse(
        TokenResponse.from(authResponseDTO.accessToken()),
        TokenResponse.from(authResponseDTO.refreshToken()),
        UsuarioResponse.from(authResponseDTO.usuario()));
  }
}
