package com.gamelisto.publicaciones.infrastructure.in.api.dto;

import com.gamelisto.publicaciones.application.usecases.PublicacionDetalleResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record PublicacionDetalleResponse(
    String id,
    String autorId,
    String gameId,
    String titulo,
    String idioma,
    String experiencia,
    String estiloJuego,
    int jugadoresMaximos,
    int participantesCount,
    int plazasDisponibles,
    List<UsuarioRefResponse> participantes,
    Map<String, Set<String>> disponibilidad) {
  public static PublicacionDetalleResponse from(PublicacionDetalleResult r) {
    return new PublicacionDetalleResponse(
        r.id(),
        r.autorId(),
        r.gameId(),
        r.titulo(),
        r.idioma(),
        r.experiencia(),
        r.estiloJuego(),
        r.jugadoresMaximos(),
        r.participantesCount(),
        r.plazasDisponibles(),
        r.participantes().stream().map(UsuarioRefResponse::from).toList(),
        r.disponibilidad());
  }
}
