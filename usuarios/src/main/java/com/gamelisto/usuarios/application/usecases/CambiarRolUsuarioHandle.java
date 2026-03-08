package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.CambiarRolUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioResult;

public interface CambiarRolUsuarioHandle {
  UsuarioResult execute(CambiarRolUsuarioCommand command);
}
