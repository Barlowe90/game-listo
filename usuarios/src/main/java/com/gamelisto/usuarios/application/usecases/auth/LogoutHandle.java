package com.gamelisto.usuarios.application.usecases.auth;

import com.gamelisto.usuarios.application.dto.LogoutCommand;

public interface LogoutHandle {
  void execute(LogoutCommand command);
}
