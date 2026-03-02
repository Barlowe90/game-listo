package com.gamelisto.publicaciones.application.usecases;

import java.util.List;
import java.util.UUID;

public interface BuscarSolicitudesUnionEnviadasHandler {
  List<SolicitudUnionResult> execute(UUID userId);
}
