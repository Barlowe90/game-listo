package com.gamelisto.usuarios_service.application.dto;

public record RefreshTokenCommand(String refreshToken) {

  public RefreshTokenCommand {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new IllegalArgumentException("El refresh token no puede estar vacío");
    }
  }
}
