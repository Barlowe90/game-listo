package com.gamelisto.usuarios.application.usecases.usuarios;

import com.gamelisto.usuarios.application.dto.CambiarCorreoCommand;

public interface CambiarCorreoHandle {
  void execute(CambiarCorreoCommand command);
}
