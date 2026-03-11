package com.gamelisto.usuarios.application.usecases.auth;

import com.gamelisto.usuarios.application.dto.CrearUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioResult;

public interface CrearUsuarioHandle {
  UsuarioResult execute(CrearUsuarioCommand command);
}
