package com.gamelisto.biblioteca.application.usecase;

import java.util.UUID;

public interface AddGameToListHandler {
  ListaGameResult execute(UUID userId, String listaId, String gameId);
}
