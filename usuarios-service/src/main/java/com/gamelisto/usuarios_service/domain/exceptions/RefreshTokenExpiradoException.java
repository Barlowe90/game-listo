package com.gamelisto.usuarios_service.domain.exceptions;

public class RefreshTokenExpiradoException extends RuntimeException {

  public RefreshTokenExpiradoException(String mensaje) {
    super(mensaje);
  }
}
