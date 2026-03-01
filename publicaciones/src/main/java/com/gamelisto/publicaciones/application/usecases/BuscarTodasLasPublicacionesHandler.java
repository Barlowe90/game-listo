package com.gamelisto.publicaciones.application.usecases;

import java.util.List;

public interface BuscarTodasLasPublicacionesHandler {
  List<PublicacionResult> execute();
}
