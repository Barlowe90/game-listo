package com.gamelisto.usuarios.application.dto;

public record AuthResponseResult(
    TokenDTO accessToken, TokenDTO refreshToken, UsuarioResult usuario) {

  public AuthResponseResult {
    if (accessToken == null) {
      throw new IllegalArgumentException("El access token no puede ser nulo");
    }
    if (refreshToken == null) {
      throw new IllegalArgumentException("El refresh token no puede ser nulo");
    }
    if (usuario == null) {
      throw new IllegalArgumentException("El usuario no puede ser nulo");
    }
  }
}
