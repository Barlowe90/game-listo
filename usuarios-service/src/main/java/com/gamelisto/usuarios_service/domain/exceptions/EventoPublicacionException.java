package com.gamelisto.usuarios_service.domain.exceptions;

public class EventoPublicacionException extends RuntimeException {
  public EventoPublicacionException(String message) {
    super(message);
  }

  public EventoPublicacionException(String message, Throwable cause) {
    super(message, cause);
  }
}
