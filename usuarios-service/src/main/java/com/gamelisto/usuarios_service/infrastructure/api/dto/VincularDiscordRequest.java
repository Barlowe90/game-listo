package com.gamelisto.usuarios_service.infrastructure.api.dto;

import com.gamelisto.usuarios_service.application.dto.VincularDiscordCommand;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;

public record VincularDiscordRequest(
    @NotBlank(message = "El ID de usuario de Discord es obligatorio") @NonNull String discordUserId,
    @NotBlank(message = "El nombre de usuario de Discord es obligatorio") @NonNull String discordUsername) {
  public VincularDiscordCommand toCommand(@NonNull String usuarioId) {
    return new VincularDiscordCommand(usuarioId, discordUserId, discordUsername);
  }
}
