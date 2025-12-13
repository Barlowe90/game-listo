package com.gamelisto.usuarios_service.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;

import com.gamelisto.usuarios_service.application.dto.VincularDiscordCommand;

public record VincularDiscordRequest(
    @NotBlank(message = "El código de autorización es obligatorio")
    String code,
    
    @NotBlank(message = "La URI de redirección es obligatoria")
    String redirectUri
) {
    public VincularDiscordCommand toCommand(String usuarioId) {
        return new VincularDiscordCommand(usuarioId, code, redirectUri);
    }
}
