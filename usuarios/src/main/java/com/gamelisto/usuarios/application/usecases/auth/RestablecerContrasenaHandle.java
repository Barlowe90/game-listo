package com.gamelisto.usuarios.application.usecases.auth;

import com.gamelisto.usuarios.application.dto.RestablecerContrasenaCommand;

public interface RestablecerContrasenaHandle {
  void execute(RestablecerContrasenaCommand command);
}
