package com.gamelisto.usuarios.infrastructure.in.api.dto;

import jakarta.validation.constraints.NotBlank;

// Request para cerrar sesión y revocar tokens
public record LogoutRequest(
    @NotBlank(message = "El refresh token es obligatorio") String refreshToken,
    String accessToken) {}
