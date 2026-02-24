package com.gamelisto.biblioteca.application.usecase.editarlistagame;

import com.gamelisto.biblioteca.domain.listas.ListaGame;

/** crear solo lo que el caso de uso necesita exponer */
public record EditarListaGameResult(String id, String usuarioRefId, String nombre, String tipo) {

  public static EditarListaGameResult from(ListaGame listaGame) {
    return new EditarListaGameResult(
        listaGame.getId().value().toString(),
        listaGame.getUsuarioRefId().toString(),
        listaGame.getNombreLista().toString(),
        listaGame.getTipo().toString());
  }
}
