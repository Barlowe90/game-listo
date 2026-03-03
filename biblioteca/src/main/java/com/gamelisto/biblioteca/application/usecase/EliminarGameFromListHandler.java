package com.gamelisto.biblioteca.application.usecase;

public interface EliminarGameFromListHandler {
  ListaGameResult execute(String userId, String listaId, String gameId);
}
