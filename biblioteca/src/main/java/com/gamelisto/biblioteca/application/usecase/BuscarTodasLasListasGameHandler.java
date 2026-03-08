package com.gamelisto.biblioteca.application.usecase;

import java.util.List;
import java.util.UUID;

public interface BuscarTodasLasListasGameHandler {
  List<ListaGameResult> execute(UUID userId);
}
