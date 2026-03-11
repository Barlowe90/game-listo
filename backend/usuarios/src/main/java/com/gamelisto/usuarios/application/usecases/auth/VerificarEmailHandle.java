package com.gamelisto.usuarios.application.usecases.auth;

import com.gamelisto.usuarios.application.dto.VerificarEmailCommand;

public interface VerificarEmailHandle {
  void execute(VerificarEmailCommand command);
}
