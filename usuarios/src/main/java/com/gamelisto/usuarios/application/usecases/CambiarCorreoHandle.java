package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.CambiarCorreoCommand;

public interface CambiarCorreoHandle {
  void execute(CambiarCorreoCommand command);
}
