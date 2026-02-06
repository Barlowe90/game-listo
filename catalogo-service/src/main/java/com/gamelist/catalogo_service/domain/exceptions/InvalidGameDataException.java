package com.gamelist.catalogo_service.domain.exceptions;

/** Excepción lanzada cuando los datos de un juego son inválidos. */
public class InvalidGameDataException extends RuntimeException {

  public InvalidGameDataException(String message) {
    super(message);
  }

  public InvalidGameDataException(String message, Throwable cause) {
    super(message, cause);
  }
}
