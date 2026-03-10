package com.gamelisto.biblioteca.application.usecase;

import java.util.UUID;

public interface EliminarListaGameHandler {
  void execute(UUID userId, String listaId);
}
