package com.gamelisto.busquedas.application.usecases;

import java.util.List;

public interface EntradaEventosHandle {

  void procesarGameCreado(Long gameId, String title, List<String> alternativeNames);
}
