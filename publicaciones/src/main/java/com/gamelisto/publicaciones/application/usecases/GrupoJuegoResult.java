package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.GrupoJuego;

import java.time.Instant;
import java.util.UUID;

public record GrupoJuegoResult(UUID id, UUID publicacionId, Instant fechaCreacion) {

  public static GrupoJuegoResult from(GrupoJuego grupoJuego) {
    return new GrupoJuegoResult(
        grupoJuego.getId().value(),
        grupoJuego.getPublicacionId().value(),
        grupoJuego.getFechaCreacion());
  }
}
