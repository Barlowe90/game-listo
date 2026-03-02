package com.gamelisto.publicaciones.application.usecases;

import java.util.UUID;

public interface EliminarPublicacionHandler {
  void execute(UUID publicacionId, UUID userId);
}
