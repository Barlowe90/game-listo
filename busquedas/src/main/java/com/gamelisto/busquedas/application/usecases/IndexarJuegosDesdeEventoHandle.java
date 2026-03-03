package com.gamelisto.busquedas.application.usecases;

import com.gamelisto.busquedas.infrastructure.messaging.dto.VideojuegoCreadoEventDto;

public interface IndexarJuegosDesdeEventoHandle {
  void execute(VideojuegoCreadoEventDto dto);
}
