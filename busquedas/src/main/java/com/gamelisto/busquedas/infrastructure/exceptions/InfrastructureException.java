package com.gamelisto.busquedas.infrastructure.exceptions;

public class InfrastructureException extends RuntimeException {

  public InfrastructureException(String message) {
    super(message);
  }

  public InfrastructureException(String message, Throwable cause) {
    super(message, cause);
  }
}
