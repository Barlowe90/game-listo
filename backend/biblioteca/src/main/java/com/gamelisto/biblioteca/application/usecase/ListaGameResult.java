package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.domain.ListaGame;
import java.util.List;

/** crear solo lo que el caso de uso necesita exponer */
public record ListaGameResult(
    String id,
    String usuarioRefId,
    String nombre,
    String tipo,
    List<com.gamelisto.biblioteca.application.usecase.ListaGameItemResult> juegos) {

  public static ListaGameResult from(ListaGame listaGame) {
    return new ListaGameResult(
        listaGame.getId().value().toString(),
        listaGame.getUsuarioRefId().toString(),
        listaGame.getNombreLista().toString(),
        listaGame.getTipo().toString(),
        List.of());
  }

  public static ListaGameResult from(
      ListaGame listaGame,
      List<com.gamelisto.biblioteca.application.usecase.ListaGameItemResult> juegos) {
    return new ListaGameResult(
        listaGame.getId().value().toString(),
        listaGame.getUsuarioRefId().toString(),
        listaGame.getNombreLista().toString(),
        listaGame.getTipo().toString(),
        juegos != null ? List.copyOf(juegos) : List.of());
  }
}
