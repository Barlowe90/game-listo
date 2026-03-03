package com.gamelisto.publicaciones.application.usecases;

import java.util.List;

public interface BuscarPublicacionesPorJuegoHandler {
  List<PublicacionResult> execute(Long gameId);
}
