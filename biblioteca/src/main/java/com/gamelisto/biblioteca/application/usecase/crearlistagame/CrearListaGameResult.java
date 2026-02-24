package com.gamelisto.biblioteca.application.usecase.crearlistagame;

import com.gamelisto.biblioteca.domain.listas.ListaGame;

/** crear solo lo que el caso de uso necesita exponer */
public record CrearListaGameResult(String id, String usuarioRefId, String nombre, String tipo) {
  public static CrearListaGameResult from(ListaGame listaGame) {
    return new CrearListaGameResult(
        listaGame.getId().value().toString(),
        listaGame.getUsuarioRefId().toString(),
        listaGame.getNombreLista().toString(),
        listaGame.getTipo().toString());
  }
}
