package com.gamelisto.biblioteca.application.usecase.eliminarlista;

import com.gamelisto.biblioteca.application.usecase.editarlistagame.EditarListaGameResult;

public interface EliminarListaGameHandler {
  void execute(String idLista);
}
