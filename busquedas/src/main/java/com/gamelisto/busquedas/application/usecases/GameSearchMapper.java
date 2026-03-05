package com.gamelisto.busquedas.application.usecases;

import com.gamelisto.busquedas.domain.BuscarJuegoDoc;
import com.gamelisto.busquedas.infrastructure.in.messaging.GameCreadoEventDto;

import java.util.List;

public class GameSearchMapper {

  private GameSearchMapper() {
    // Utilidad estática
  }

  public static BuscarJuegoDoc fromEvent(GameCreadoEventDto dto) {
    List<String> altNames = dto.alternativeNames() == null ? List.of() : dto.alternativeNames();
    return new BuscarJuegoDoc(dto.id(), dto.name(), altNames);
  }
}
