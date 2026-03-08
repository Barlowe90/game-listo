package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.SolicitarRestablecimientoCommand;

public interface SolicitarRestablecimientoHandle {
  void execute(SolicitarRestablecimientoCommand command);
}
