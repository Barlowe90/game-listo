package com.gamelisto.usuarios.application.usecases.admin;

import com.gamelisto.usuarios.application.dto.CambiarEstadoUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioResult;

public interface CambiarEstadoUsuarioHandle {
  UsuarioResult execute(CambiarEstadoUsuarioCommand command);
}
