package com.gamelisto.usuarios_service.domain.exceptions;

public class RefreshTokenInvalidoException extends RuntimeException {

  public RefreshTokenInvalidoException(String mensaje) {
    super(mensaje);
  }
}
