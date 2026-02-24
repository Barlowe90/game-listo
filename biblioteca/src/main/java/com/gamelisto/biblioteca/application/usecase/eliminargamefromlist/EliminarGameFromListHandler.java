package com.gamelisto.biblioteca.application.usecase.eliminargamefromlist;

import com.gamelisto.biblioteca.application.usecase.ListaGameResult;

public interface EliminarGameFromListHandler {
  ListaGameResult execute(String listaId, String gameId);
}
