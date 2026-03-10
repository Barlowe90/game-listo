package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.UsuarioResult;

public interface BuscarUsuariosPorNombreHandle {
  UsuarioResult execute(String username);
}
