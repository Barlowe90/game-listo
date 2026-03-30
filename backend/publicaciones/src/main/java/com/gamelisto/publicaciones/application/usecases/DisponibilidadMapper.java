package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.DiaSemana;
import com.gamelisto.publicaciones.domain.FranjaHoraria;
import com.gamelisto.publicaciones.domain.vo.DisponibilidadSemanal;

import java.util.*;
import java.util.stream.Collectors;

public final class DisponibilidadMapper {

  private DisponibilidadMapper() {}

  public static DisponibilidadSemanal mapToDomainDisponibilidad(Map<String, Set<String>> in) {
    if (in == null) return DisponibilidadSemanal.empty();
    Map<DiaSemana, Set<FranjaHoraria>> map = new EnumMap<>(DiaSemana.class);
    in.forEach(
        (k, v) -> {
          try {
            DiaSemana dia = DiaSemana.valueOf(k);
            Set<FranjaHoraria> franjas =
                v.stream().map(FranjaHoraria::valueOf).collect(Collectors.toSet());
            map.put(dia, franjas);
          } catch (IllegalArgumentException ex) {
            // ignore invalid keys/values
          }
        });
    return DisponibilidadSemanal.of(map);
  }
}

