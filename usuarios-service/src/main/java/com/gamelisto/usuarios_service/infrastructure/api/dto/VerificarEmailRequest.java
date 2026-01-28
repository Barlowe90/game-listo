package com.gamelisto.usuarios_service.infrastructure.api.dto;

import com.gamelisto.usuarios_service.application.dto.VerificarEmailCommand;

import jakarta.validation.constraints.NotBlank;

public record VerificarEmailRequest (
    @NotBlank(message = "El token de verificación es obligatorio")
    String token
) {
    public VerificarEmailCommand toCommand() {
        return new VerificarEmailCommand(token);
    }
}
