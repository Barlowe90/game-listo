package com.gamelisto.publicaciones.application.usecases;

import java.util.UUID;

public interface PeticionUnionPublicacionHandler {
  PeticionUnionResult execute(UUID publicacionId, UUID userId);
}
