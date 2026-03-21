package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.FranjaHoraria;
import com.gamelisto.publicaciones.domain.GrupoJuego;
import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.UsuarioRef;
import com.gamelisto.publicaciones.domain.vo.DisponibilidadSemanal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record PublicacionDetalleResult(
    String id,
    String autorId,
    String gameId,
    String titulo,
    String idioma,
    String experiencia,
    String estiloJuego,
    int jugadoresMaximos,
    String grupoId,
    int participantesCount,
    int plazasDisponibles,
    List<UsuarioRefResult> participantes,
    Map<String, Set<String>> disponibilidad) {

  public static PublicacionDetalleResult from(
      Publicacion p, GrupoJuego grupo, List<UsuarioRef> participantes) {
    int count = participantes.size();
    int plazas = Math.max(0, p.getJugadoresMaximos() - count);
    String grupoId = grupo == null ? null : grupo.getId().value().toString();

    return new PublicacionDetalleResult(
        p.getId().value().toString(),
        p.getAutorId().toString(),
        p.getGameId().toString(),
        p.getTitulo(),
        p.getIdioma().name(),
        p.getExperiencia().name(),
        p.getEstiloJuego().name(),
        p.getJugadoresMaximos(),
        grupoId,
        count,
        plazas,
        participantes.stream().map(UsuarioRefResult::from).toList(),
        mapDisponibilidad(p.getDisponibilidadSemanal()));
  }

  private static Map<String, Set<String>> mapDisponibilidad(DisponibilidadSemanal ds) {
    if (ds == null) return null;
    return ds.value().entrySet().stream()
        .collect(
            Collectors.toMap(
                e -> e.getKey().name(),
                e -> e.getValue().stream().map(FranjaHoraria::name).collect(Collectors.toSet())));
  }
}
