package com.gamelisto.usuarios.application.dto;

public record LoginCommand(String email, String password) {

  public LoginCommand {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("El email no puede estar vacío");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("La contraseña no puede estar vacía");
    }
  }
}
