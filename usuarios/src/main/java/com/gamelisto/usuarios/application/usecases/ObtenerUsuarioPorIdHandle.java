package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.UsuarioResult;

public interface ObtenerUsuarioPorIdHandle {
  UsuarioResult execute(String usuarioId);
}
