package com.gamelisto.publicaciones.infrastructure.in.api.dto;

import com.gamelisto.publicaciones.application.usecases.PublicacionResult;

public record PublicacionResponse(
    String id,
    String autorId,
    String gameId,
    String titulo,
    String idioma,
    String experiencia,
    String estiloJuego,
    int jugadoresMaximos,
    String grupoId) {

  public static PublicacionResponse from(PublicacionResult p) {
    return new PublicacionResponse(
        p.id(),
        p.autorId(),
        p.gameId(),
        p.titulo(),
        p.idioma(),
        p.experiencia(),
        p.estiloJuego(),
        p.jugadoresMaximos(),
        p.grupoId());
  }
}
