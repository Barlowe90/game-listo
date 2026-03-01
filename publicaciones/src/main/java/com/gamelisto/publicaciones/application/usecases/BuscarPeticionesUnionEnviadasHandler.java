package com.gamelisto.publicaciones.application.usecases;

import java.util.List;
import java.util.UUID;

public interface BuscarPeticionesUnionEnviadasHandler {
  List<PeticionUnionResult> execute(UUID userId);
}
