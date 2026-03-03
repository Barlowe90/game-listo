package com.gamelisto.publicaciones.application.usecases;

import java.util.UUID;

public interface CrearSolicitudUnionHandler {
  SolicitudUnionResult execute(UUID publicacionId, UUID userId);
}
