package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.GameEstadoResult;

public record GameEstadoResponse(
    String id, String usuarioRefId, Long gameId, String estado, Double rating) {

  public static GameEstadoResponse from(GameEstadoResult r) {
    return new GameEstadoResponse(r.id(), r.usuarioRefId(), r.gameId(), r.estado(), r.rating());
  }
}
