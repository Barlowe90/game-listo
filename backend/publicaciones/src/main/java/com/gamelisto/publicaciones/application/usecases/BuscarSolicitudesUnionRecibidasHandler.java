package com.gamelisto.publicaciones.application.usecases;

import java.util.List;
import java.util.UUID;

public interface BuscarSolicitudesUnionRecibidasHandler {
  List<SolicitudUnionResult> execute(UUID userId);
}
