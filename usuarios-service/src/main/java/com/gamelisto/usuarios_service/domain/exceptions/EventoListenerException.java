package com.gamelisto.usuarios_service.domain.exceptions;

public class EventoListenerException extends RuntimeException {
  public EventoListenerException(String message) {
    super(message);
  }

  public EventoListenerException(String message, Throwable cause) {
    super(message, cause);
  }
}
