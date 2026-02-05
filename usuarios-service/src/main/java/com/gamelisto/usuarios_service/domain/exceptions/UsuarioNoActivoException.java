package com.gamelisto.usuarios_service.domain.exceptions;

public class UsuarioNoActivoException extends RuntimeException {

  public UsuarioNoActivoException(String mensaje) {
    super(mensaje);
  }
}
