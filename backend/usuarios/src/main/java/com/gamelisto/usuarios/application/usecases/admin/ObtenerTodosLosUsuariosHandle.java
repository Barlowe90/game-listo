package com.gamelisto.usuarios.application.usecases.admin;

import com.gamelisto.usuarios.application.dto.UsuarioResult;

import java.util.List;

public interface ObtenerTodosLosUsuariosHandle {
  List<UsuarioResult> execute();
}
