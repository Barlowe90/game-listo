package com.gamelisto.usuarios.application.dto;

public record LogoutCommand(String refreshToken, String accessToken) {

  public LogoutCommand {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new IllegalArgumentException("El refresh token no puede estar vacío");
    }
  }
}
