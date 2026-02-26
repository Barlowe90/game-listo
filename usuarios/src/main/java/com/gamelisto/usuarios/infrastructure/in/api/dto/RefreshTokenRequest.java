package com.gamelisto.usuarios.infrastructure.in.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para renovar access token usando refresh token")
public record RefreshTokenRequest(
    @Schema(
            description = "Refresh token válido",
            example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken) {}
