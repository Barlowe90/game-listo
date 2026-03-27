package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.GrupoJuego;
import com.gamelisto.publicaciones.domain.vo.DisponibilidadSemanal;
import java.util.Collections;
import com.gamelisto.publicaciones.domain.FranjaHoraria;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record PublicacionResult(
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
        null,
        mapDisponibilidad(p.getDisponibilidadSemanal()));
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
        grupoId,
        mapDisponibilidad(p.getDisponibilidadSemanal()));
  }

  private static Map<String, Set<String>> mapDisponibilidad(DisponibilidadSemanal ds) {
    if (ds == null) return Collections.emptyMap();
    return ds.value().entrySet().stream()
        .collect(
            Collectors.toMap(
                e -> e.getKey().name(),
                e -> e.getValue().stream().map(FranjaHoraria::name).collect(Collectors.toSet())));
  }
}
