package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.RestablecerContrasenaCommand;

public interface RestablecerContrasenaHandle {
  void execute(RestablecerContrasenaCommand command);
}
