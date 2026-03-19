package com.gamelisto.biblioteca.application.usecase;

import java.util.UUID;

public interface EliminarGameEstadoHandler {
  void execute(UUID userId, String gameId);
}
