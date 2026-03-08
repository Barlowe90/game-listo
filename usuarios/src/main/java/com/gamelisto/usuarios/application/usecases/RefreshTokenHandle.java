package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.AuthResponseResult;
import com.gamelisto.usuarios.application.dto.RefreshTokenCommand;

public interface RefreshTokenHandle {
  AuthResponseResult execute(RefreshTokenCommand command);
}
