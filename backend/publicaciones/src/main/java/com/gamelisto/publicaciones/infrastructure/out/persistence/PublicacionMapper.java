package com.gamelisto.publicaciones.infrastructure.out.persistence;

import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.DiaSemana;
import com.gamelisto.publicaciones.domain.FranjaHoraria;
import com.gamelisto.publicaciones.domain.vo.DisponibilidadSemanal;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PublicacionMapper {
  public PublicacionDocument toDocument(Publicacion publicacion) {
    PublicacionDocument document = new PublicacionDocument();
    document.setId(publicacion.getId().value());
    document.setAutorId(publicacion.getAutorId());
    document.setGameId(publicacion.getGameId());
    document.setTitulo(publicacion.getTitulo());
    document.setIdioma(publicacion.getIdioma());
    document.setExperiencia(publicacion.getExperiencia());
    document.setEstiloJuego(publicacion.getEstiloJuego());
    document.setJugadoresMaximos(publicacion.getJugadoresMaximos());

    DisponibilidadSemanal ds = publicacion.getDisponibilidadSemanal();
    if (ds != null) {
      Map<String, Set<String>> disponibilidad =
          ds.value().entrySet().stream()
              .collect(
                  Collectors.toMap(
                      e -> e.getKey().name(),
                      e ->
                          e.getValue().stream()
                              .map(FranjaHoraria::name)
                              .collect(Collectors.toSet())));
      document.setDisponibilidad(disponibilidad);
    }

    return document;
  }

  public Publicacion toDomain(PublicacionDocument document) {
    DisponibilidadSemanal disponibilidad = null;
    if (document.getDisponibilidad() != null) {
      Map<DiaSemana, Set<FranjaHoraria>> map = new EnumMap<>(DiaSemana.class);
      document
          .getDisponibilidad()
          .forEach(
              (k, v) -> {
                try {
                  DiaSemana dia = DiaSemana.valueOf(k);
                  Set<FranjaHoraria> franjas =
                      v.stream().map(FranjaHoraria::valueOf).collect(Collectors.toSet());
                  map.put(dia, franjas);
                } catch (IllegalArgumentException ex) {
                  // ignore unknown keys/values
                }
              });
      disponibilidad = DisponibilidadSemanal.of(map);
    }

    return Publicacion.reconstitute(
        document.getId(),
        document.getAutorId(),
        document.getGameId(),
        document.getTitulo(),
        document.getIdioma(),
        document.getExperiencia(),
        document.getEstiloJuego(),
        document.getJugadoresMaximos(),
        disponibilidad);
  }
}
