package com.gamelisto.usuarios.application.usecases.auth;

import com.gamelisto.usuarios.application.dto.AuthResponseResult;
import com.gamelisto.usuarios.application.dto.LoginCommand;

public interface LoginUseHandle {
  AuthResponseResult execute(LoginCommand command);
}
