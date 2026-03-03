package com.gamelisto.publicaciones.application.usecases;

import java.util.List;
import java.util.UUID;

public interface BuscarPublicacionesUsuarioHandler {
  List<PublicacionResult> execute(UUID userId);
}
