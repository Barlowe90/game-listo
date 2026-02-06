package com.gamelist.catalogo_service.domain.exceptions;

public class GameNotFoundException extends RuntimeException {

  public GameNotFoundException(Long gameId) {
    super("Juego no encontrado con ID: " + gameId);
  }

  public GameNotFoundException(String message) {
    super(message);
  }
}
