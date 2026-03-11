package com.gamelisto.usuarios.application.usecases.discord;

import com.gamelisto.usuarios.application.dto.UsuarioResult;

import java.util.UUID;

public interface DesvincularDiscordHandle {
  UsuarioResult execute(UUID usuarioId);
}
