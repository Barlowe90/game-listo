package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.domain.listas.ListaGame;

/** crear solo lo que el caso de uso necesita exponer */
public record ListaGameResult(String id, String usuarioRefId, String nombre, String tipo) {

  public static ListaGameResult from(ListaGame listaGame) {
    return new ListaGameResult(
        listaGame.getId().value().toString(),
        listaGame.getUsuarioRefId().toString(),
        listaGame.getNombreLista().toString(),
        listaGame.getTipo().toString());
  }
}
