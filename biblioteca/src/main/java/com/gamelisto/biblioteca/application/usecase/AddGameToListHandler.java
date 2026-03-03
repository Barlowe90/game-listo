package com.gamelisto.biblioteca.application.usecase;

public interface AddGameToListHandler {
  ListaGameResult execute(String userId, String listaId, String gameId);
}
