package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.GrupoJuego;

public record PublicacionResult(
    String id,
    String autorId,
    String gameId,
    String titulo,
    String idioma,
    String experiencia,
    String estiloJuego,
    int jugadoresMaximos,
    String grupoId) {

  public static PublicacionResult from(Publicacion p) {
    return new PublicacionResult(
        p.getId().value().toString(),
        p.getAutorId().toString(),
        p.getGameId().toString(),
        p.getTitulo(),
        p.getIdioma().toString(),
        p.getExperiencia().toString(),
        p.getEstiloJuego().toString(),
        p.getJugadoresMaximos(),
        null);
  }

  public static PublicacionResult from(Publicacion p, GrupoJuego grupo) {
    String grupoId = grupo == null ? null : grupo.getId().value().toString();
    return new PublicacionResult(
        p.getId().value().toString(),
        p.getAutorId().toString(),
        p.getGameId().toString(),
        p.getTitulo(),
        p.getIdioma().toString(),
        p.getExperiencia().toString(),
        p.getEstiloJuego().toString(),
        p.getJugadoresMaximos(),
        grupoId);
  }
}
