package com.gamelisto.publicaciones.infrastructure.in.api.dto;

import com.gamelisto.publicaciones.application.usecases.PublicacionResult;

import java.util.Map;
import java.util.Set;

public record PublicacionResponse(
    String id,
    String autorId,
    String gameId,
    String titulo,
    String idioma,
    String experiencia,
    String estiloJuego,
    int jugadoresMaximos,
    String grupoId,
    Map<String, Set<String>> disponibilidad) {

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
        p.grupoId(),
        p.disponibilidad());
  }
}
