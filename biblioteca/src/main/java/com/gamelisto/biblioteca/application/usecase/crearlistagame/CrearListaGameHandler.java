package com.gamelisto.biblioteca.application.usecase.crearlistagame;

import com.gamelisto.biblioteca.domain.listas.ListaGame;

public interface CrearListaGameHandler {
  ListaGame execute(CrearListaGameCommand command);
}
