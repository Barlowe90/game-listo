package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.Publicacion;

public record PublicacionResult(
    String id,
    String autorId,
    String gameId,
    String titulo,
    String idioma,
    String experiencia,
    String estiloJuego,
    int jugadoresMaximos,
    String estadoPublicacion) {

  public static PublicacionResult from(Publicacion p) {
    return new PublicacionResult(
        p.getId().toString(),
        p.getAutorId().toString(),
        p.getGameId().toString(),
        p.getTitulo(),
        p.getIdioma().toString(),
        p.getExperiencia().toString(),
        p.getEstiloJuego().toString(),
        p.getJugadoresMaximos(),
        p.getEstadoPublicacion().toString());
  }
}
