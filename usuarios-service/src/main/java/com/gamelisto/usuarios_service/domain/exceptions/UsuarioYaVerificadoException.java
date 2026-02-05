package com.gamelisto.usuarios_service.domain.exceptions;

import lombok.Getter;

@Getter
public class UsuarioYaVerificadoException extends RuntimeException {

  private final String email;

  public UsuarioYaVerificadoException(String email) {
    super("El usuario con email " + email + " ya ha sido verificado");
    this.email = email;
  }
}
