package com.gamelisto.publicaciones.domain.vo;

import com.gamelisto.publicaciones.domain.DiaSemana;
import com.gamelisto.publicaciones.domain.FranjaHoraria;

import java.util.*;

public class DisponibilidadSemanal {

  private final Map<DiaSemana, Set<FranjaHoraria>> disponibilidad;

  private DisponibilidadSemanal(Map<DiaSemana, Set<FranjaHoraria>> disponibilidad) {
    EnumMap<DiaSemana, Set<FranjaHoraria>> normalizada = new EnumMap<>(DiaSemana.class);

    for (DiaSemana dia : DiaSemana.values()) {
      Set<FranjaHoraria> franjas = disponibilidad.getOrDefault(dia, Set.of());
      normalizada.put(
          dia, EnumSet.copyOf(franjas.isEmpty() ? EnumSet.noneOf(FranjaHoraria.class) : franjas));
    }

    this.disponibilidad = Collections.unmodifiableMap(normalizada);
  }

  public static DisponibilidadSemanal empty() {
    Map<DiaSemana, Set<FranjaHoraria>> vacia = new EnumMap<>(DiaSemana.class);
    for (DiaSemana dia : DiaSemana.values()) {
      vacia.put(dia, EnumSet.noneOf(FranjaHoraria.class));
    }
    return new DisponibilidadSemanal(vacia);
  }

  public static DisponibilidadSemanal of(Map<DiaSemana, Set<FranjaHoraria>> disponibilidad) {
    return new DisponibilidadSemanal(disponibilidad);
  }

  public boolean estaDisponible(DiaSemana dia, FranjaHoraria franja) {
    return disponibilidad.getOrDefault(dia, Set.of()).contains(franja);
  }

  public Map<DiaSemana, Set<FranjaHoraria>> value() {
    return disponibilidad;
  }
}
