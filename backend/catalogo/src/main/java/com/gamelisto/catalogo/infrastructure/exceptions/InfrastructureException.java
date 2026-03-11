package com.gamelisto.catalogo.infrastructure.exceptions;

public class InfrastructureException extends RuntimeException {

  public InfrastructureException(String message) {
    super(message);
  }

  public InfrastructureException(String message, Throwable cause) {
    super(message, cause);
  }
}
