package com.gamelisto.publicaciones.infraestructure.in.api.dto;

import com.gamelisto.publicaciones.application.usecases.PublicacionDetalleResult;

import java.util.List;

public record PublicacionDetalleResponse(
    String id,
    String autorId,
    String gameId,
    String titulo,
    String idioma,
    String experiencia,
    String estiloJuego,
    int jugadoresMaximos,
    String estadoPublicacion,
    int participantesCount,
    int plazasDisponibles,
    List<UsuarioRefResponse> participantes) {
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
        r.estadoPublicacion(),
        r.participantesCount(),
        r.plazasDisponibles(),
        r.participantes().stream().map(UsuarioRefResponse::from).toList());
  }
}
