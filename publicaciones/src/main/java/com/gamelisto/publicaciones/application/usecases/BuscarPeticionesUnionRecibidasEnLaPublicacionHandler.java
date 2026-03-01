package com.gamelisto.publicaciones.application.usecases;

import java.util.List;
import java.util.UUID;

public interface BuscarPeticionesUnionRecibidasEnLaPublicacionHandler {
  List<PeticionUnionResult> execute(UUID userId, UUID publicacionId);
}
