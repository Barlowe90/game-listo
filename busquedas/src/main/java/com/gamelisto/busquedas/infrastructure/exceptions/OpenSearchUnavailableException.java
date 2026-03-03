package com.gamelisto.busquedas.infrastructure.exceptions;

/** Excepción que se lanza cuando OpenSearch no está disponible o responde con error. */
public class OpenSearchUnavailableException extends RuntimeException {

  public OpenSearchUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
