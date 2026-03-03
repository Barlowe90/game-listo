package com.gamelisto.busquedas.application.usecases;

import com.gamelisto.busquedas.domain.BuscarJuegoDoc;
import com.gamelisto.busquedas.infrastructure.messaging.dto.VideojuegoCreadoEventDto;

import java.util.List;

public class GameSearchMapper {

  private GameSearchMapper() {
    // Utilidad estática
  }

  public static BuscarJuegoDoc fromEvent(VideojuegoCreadoEventDto dto) {
    List<String> altNames = dto.alternativeNames() == null ? List.of() : dto.alternativeNames();
    return new BuscarJuegoDoc(dto.gameId(), dto.title(), altNames);
  }
}
