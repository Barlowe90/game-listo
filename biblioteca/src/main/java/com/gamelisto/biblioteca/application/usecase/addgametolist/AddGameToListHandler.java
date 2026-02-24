package com.gamelisto.biblioteca.application.usecase.addgametolist;

import com.gamelisto.biblioteca.application.usecase.ListaGameResult;

public interface AddGameToListHandler {
  ListaGameResult execute(String listaId, String gameId);
}
