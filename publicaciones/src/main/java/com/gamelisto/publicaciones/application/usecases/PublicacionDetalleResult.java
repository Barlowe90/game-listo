package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.UsuarioRef;

import java.util.List;

public record PublicacionDetalleResult(
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
    List<UsuarioRefResult> participantes) {

  public static PublicacionDetalleResult from(Publicacion p, List<UsuarioRef> participantes) {
    int count = participantes.size();
    int plazas = Math.max(0, p.getJugadoresMaximos() - count);

    return new PublicacionDetalleResult(
        p.getId().toString(),
        p.getAutorId().toString(),
        p.getGameId().toString(),
        p.getTitulo(),
        p.getIdioma().name(),
        p.getExperiencia().name(),
        p.getEstiloJuego().name(),
        p.getJugadoresMaximos(),
        p.getEstadoPublicacion().name(),
        count,
        plazas,
        participantes.stream().map(UsuarioRefResult::from).toList());
  }
}
