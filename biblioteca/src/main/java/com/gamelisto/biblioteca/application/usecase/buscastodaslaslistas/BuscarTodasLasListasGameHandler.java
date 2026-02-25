package com.gamelisto.biblioteca.application.usecase.buscastodaslaslistas;

import com.gamelisto.biblioteca.application.usecase.ListaGameResult;

public interface BuscarTodasLasListasGameHandler {
  ListaGameResult execute(String userId);
}
