package com.gamelisto.publicaciones.application.usecases;

import java.util.List;
import java.util.UUID;

public interface BuscarSolicitudesUnionRecibidasEnLaPublicacionHandler {
  List<SolicitudUnionResult> execute(UUID userId, UUID publicacionId);
}
