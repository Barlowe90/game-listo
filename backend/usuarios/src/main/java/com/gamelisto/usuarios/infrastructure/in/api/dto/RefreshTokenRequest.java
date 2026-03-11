package com.gamelisto.usuarios.infrastructure.in.api.dto;

import jakarta.validation.constraints.NotBlank;

// Request para renovar access token usando refresh token
public record RefreshTokenRequest(
    @NotBlank(message = "El refresh token es obligatorio") String refreshToken) {}
