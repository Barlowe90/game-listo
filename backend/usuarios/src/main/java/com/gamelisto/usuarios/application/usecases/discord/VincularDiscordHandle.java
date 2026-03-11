package com.gamelisto.usuarios.application.usecases.discord;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.dto.VincularDiscordCommand;

public interface VincularDiscordHandle {
  UsuarioResult execute(VincularDiscordCommand command);
}
