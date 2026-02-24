package com.gamelisto.biblioteca.application.usecase.crearlistagame;

import com.gamelisto.biblioteca.application.usecase.ListaGameResult;

public interface CrearListaGameHandler {
  ListaGameResult execute(CrearListaGameCommand command);
}
