package com.gamelisto.usuarios_service.infrastructure.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para cerrar sesión y revocar tokens")
public record LogoutRequest(
    @Schema(
            description = "Refresh token a revocar",
            example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken,
    @Schema(
            description = "Access token para revocación inmediata (opcional)",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String accessToken) {}
