package com.gamelisto.biblioteca.application.usecase.buscarlistagame;

import com.gamelisto.biblioteca.application.usecase.ListaGameResult;

public interface BuscarListaGameHandler {
  ListaGameResult execute(String listaId);
}
