package com.gamelist.catalogo_service.domain.exceptions;

public class InvalidGameDataException extends RuntimeException {

  public InvalidGameDataException(String message) {
    super(message);
  }

  public InvalidGameDataException(String message, Throwable cause) {
    super(message, cause);
  }
}
