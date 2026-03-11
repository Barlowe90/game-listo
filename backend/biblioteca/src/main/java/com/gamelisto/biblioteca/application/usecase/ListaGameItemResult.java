package com.gamelisto.biblioteca.application.usecase;

/** DTO ligero para representar un juego dentro de una lista */
public record ListaGameItemResult(Long gameId, String nombre, String cover, String estado) {

  public static ListaGameItemResult of(Long gameId, String nombre, String cover, String estado) {
    return new ListaGameItemResult(gameId, nombre, cover, estado);
  }
}
