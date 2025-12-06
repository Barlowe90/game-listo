package com.gamelisto.usuarios_service.infrastructure.api.dto;

import java.time.Instant;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;

/**
 * DTO de respuesta para la API REST - Usuario.
 * 
 */
public record UsuarioResponse(
    String id,
    String username,
    String email,
    String avatar,
    Instant createdAt,
    Instant updatedAt,
    String role,
    String language,
    boolean notificationsActive,
    String status,
    String discordUserId,
    String discordUsername,
    Instant discordLinkedAt,
    boolean discordConsent
) {
    public static UsuarioResponse from(UsuarioDTO dto) {
        return new UsuarioResponse(
            dto.id(),
            dto.username(),
            dto.email(),
            dto.avatar(),
            dto.createdAt(),
            dto.updatedAt(),
            dto.role(),
            dto.language(),
            dto.notificationsActive(),
            dto.status(),
            dto.discordUserId(),
            dto.discordUsername(),
            dto.discordLinkedAt(),
            dto.discordConsent()
        );
    }
}
