package com.gamelisto.biblioteca.application.usecase.editarlistagame;

import com.gamelisto.biblioteca.application.usecase.ListaGameResult;

public interface EditarListaGameHandler {
  ListaGameResult execute(EditarListaGameCommand command);
}
