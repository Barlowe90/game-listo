package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.UsuarioResult;

import java.util.UUID;

public interface DesvincularDiscordHandle {
  UsuarioResult execute(UUID usuarioId);
}
