package com.gamelisto.publicaciones.infrastructure.in.api.dto;

import com.gamelisto.publicaciones.application.usecases.GrupoJuegoResult;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GrupoJuegoResponse(
    UUID id, UUID publicacionId, Instant fechaCreacion, List<UsuarioRefResponse> participantes) {

  public static GrupoJuegoResponse from(GrupoJuegoResult r) {
    return new GrupoJuegoResponse(
        r.id(),
        r.publicacionId(),
        r.fechaCreacion(),
        r.participantes().stream().map(UsuarioRefResponse::from).toList());
  }
}
