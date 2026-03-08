package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.EditarPerfilUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioResult;

public interface EditarPerfilUsuarioHandle {
  UsuarioResult execute(EditarPerfilUsuarioCommand command);
}
