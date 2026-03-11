package com.gamelisto.usuarios.application.usecases.usuarios;

import com.gamelisto.usuarios.application.dto.CambiarContrasenaCommand;

public interface CambiarContrasenaHandle {
  void execute(CambiarContrasenaCommand command);
}
