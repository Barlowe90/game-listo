package com.gamelisto.biblioteca.application.usecase;

import java.util.UUID;

public interface BuscarListaGameHandler {
  ListaGameResult execute(UUID userId, String listaId);
}
