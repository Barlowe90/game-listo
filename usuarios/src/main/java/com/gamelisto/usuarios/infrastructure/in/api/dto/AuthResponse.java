package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.AuthResponseResult;

// Respuesta de autenticación exitosa con tokens y datos del usuario
public record AuthResponse(
    TokenResponse accessToken, TokenResponse refreshToken, UsuarioResponse usuario) {

  public static AuthResponse from(AuthResponseResult authResponseResult) {
    return new AuthResponse(
        TokenResponse.from(authResponseResult.accessToken()),
        TokenResponse.from(authResponseResult.refreshToken()),
        UsuarioResponse.from(authResponseResult.usuario()));
  }
}
