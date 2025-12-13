package com.gamelisto.usuarios_service.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;

import com.gamelisto.usuarios_service.application.dto.VincularDiscordCommand;

public record VincularDiscordRequest(
    @NotBlank(message = "El código de autorización es obligatorio")
    @NonNull String code,
    
    @NotBlank(message = "La URI de redirección es obligatoria")
    @NonNull String redirectUri
) {
    public VincularDiscordCommand toCommand(@NonNull String usuarioId) {
        return new VincularDiscordCommand(usuarioId, code, redirectUri);
    }
}
