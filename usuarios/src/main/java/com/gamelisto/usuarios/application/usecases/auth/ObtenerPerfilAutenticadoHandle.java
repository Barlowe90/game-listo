package com.gamelisto.usuarios.application.usecases.auth;

import com.gamelisto.usuarios.application.dto.UsuarioResult;

import java.util.UUID;

public interface ObtenerPerfilAutenticadoHandle {
  UsuarioResult execute(UUID idUsuario);
}
