package com.gamelisto.publicaciones.application.usecases;

import java.util.UUID;

public interface CrearSolicitudUnionHandler {
  PeticionUnionResult execute(UUID publicacionId, UUID userId);
}
