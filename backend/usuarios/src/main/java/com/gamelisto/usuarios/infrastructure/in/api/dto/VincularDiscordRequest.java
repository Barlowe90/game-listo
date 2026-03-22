package com.gamelisto.usuarios.infrastructure.in.api.dto;

import com.gamelisto.usuarios.application.dto.VincularDiscordCommand;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;

import java.util.UUID;

public record VincularDiscordRequest(
    @NotBlank(message = "El ID de usuario de Discord es obligatorio") @NonNull
        String discordUserId) {
  public VincularDiscordCommand toCommand(UUID usuarioId) {
    return new VincularDiscordCommand(usuarioId, discordUserId);
  }
}
