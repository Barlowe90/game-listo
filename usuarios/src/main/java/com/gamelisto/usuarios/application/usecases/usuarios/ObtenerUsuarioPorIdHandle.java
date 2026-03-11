package com.gamelisto.usuarios.application.usecases.usuarios;

import com.gamelisto.usuarios.application.dto.UsuarioResult;

public interface ObtenerUsuarioPorIdHandle {
  UsuarioResult execute(String usuarioId);
}
