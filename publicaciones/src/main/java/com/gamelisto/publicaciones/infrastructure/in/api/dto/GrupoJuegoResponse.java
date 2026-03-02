package com.gamelisto.publicaciones.infrastructure.in.api.dto;

import com.gamelisto.publicaciones.application.usecases.GrupoJuegoResult;

import java.time.Instant;
import java.util.UUID;

public record GrupoJuegoResponse(UUID id, UUID publicacionId, Instant fechaCreacion) {

  public static GrupoJuegoResponse from(GrupoJuegoResult r) {
    return new GrupoJuegoResponse(r.id(), r.publicacionId(), r.fechaCreacion());
  }
}
