package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.VincularDiscordCommand;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;

public record VincularDiscordRequest(
    @NotBlank(message = "El ID de usuario de Discord es obligatorio") @NonNull String discordUserId,
    @NotBlank(message = "El nombre de usuario de Discord es obligatorio") @NonNull
        String discordUsername) {
  public VincularDiscordCommand toCommand(@NonNull String usuarioId) {
    return new VincularDiscordCommand(usuarioId, discordUserId, discordUsername);
  }
}
