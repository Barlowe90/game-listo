package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.VerificarEmailCommand;

public interface VerificarEmailHandle {
  void execute(VerificarEmailCommand command);
}
