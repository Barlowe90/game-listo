package com.gamelisto.usuarios.application.usecases.admin;

import com.gamelisto.usuarios.application.dto.CambiarRolUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioResult;

public interface CambiarRolUsuarioHandle {
  UsuarioResult execute(CambiarRolUsuarioCommand command);
}
