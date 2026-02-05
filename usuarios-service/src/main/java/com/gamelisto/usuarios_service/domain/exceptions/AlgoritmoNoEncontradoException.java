package com.gamelisto.usuarios_service.domain.exceptions;

public class AlgoritmoNoEncontradoException extends RuntimeException {

  public AlgoritmoNoEncontradoException(String mensaje) {
    super(mensaje);
  }

  public AlgoritmoNoEncontradoException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}
