package com.gamelisto.biblioteca.application.usecase;

import java.util.List;

public interface BuscarTodasLasListasGameHandler {
  List<ListaGameResult> execute(String userId);
}
