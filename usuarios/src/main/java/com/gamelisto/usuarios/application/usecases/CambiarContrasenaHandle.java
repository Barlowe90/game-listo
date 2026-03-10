package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.CambiarContrasenaCommand;

public interface CambiarContrasenaHandle {
  void execute(CambiarContrasenaCommand command);
}
