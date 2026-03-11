package com.gamelisto.biblioteca.application.usecase;

import java.util.List;

public interface BuscarGameEstadosPorGameIdHandler {
  List<GameEstadoResult> execute(Long gameRefId);
}
