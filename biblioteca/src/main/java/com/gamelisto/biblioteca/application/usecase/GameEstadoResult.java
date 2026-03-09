package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.domain.GameEstado;

/** DTO con la información completa de GameEstado expuesta por el caso de uso */
public record GameEstadoResult(
    String id, String usuarioRefId, Long gameId, String estado, Double rating) {

  public static GameEstadoResult from(GameEstado g) {
    return new GameEstadoResult(
        g.getId().toString(),
        g.getUsuarioRefId().toString(),
        g.getGameRefId().value(),
        g.getEstado().name(),
        g.getRating() != null ? g.getRating().value() : null);
  }
}
