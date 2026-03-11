package com.gamelisto.usuarios.application.usecases.auth;

import com.gamelisto.usuarios.application.dto.ReenviarVerificacionCommand;

public interface ReenviarVerificacionHandle {
  void execute(ReenviarVerificacionCommand command);
}
