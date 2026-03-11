package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.GrupoJuego;
import com.gamelisto.publicaciones.domain.UsuarioRef;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GrupoJuegoResult(
    UUID id, UUID publicacionId, Instant fechaCreacion, List<UsuarioRefResult> participantes) {

  public static GrupoJuegoResult from(GrupoJuego grupoJuego) {
    return new GrupoJuegoResult(
        grupoJuego.getId().value(),
        grupoJuego.getPublicacionId().value(),
        grupoJuego.getFechaCreacion(),
        List.of());
  }

  public static GrupoJuegoResult from(GrupoJuego grupoJuego, List<UsuarioRef> participantes) {
    return new GrupoJuegoResult(
        grupoJuego.getId().value(),
        grupoJuego.getPublicacionId().value(),
        grupoJuego.getFechaCreacion(),
        participantes.stream().map(UsuarioRefResult::from).toList());
  }
}
