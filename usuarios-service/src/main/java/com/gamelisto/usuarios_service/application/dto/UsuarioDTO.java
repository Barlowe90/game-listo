package com.gamelisto.usuarios_service.application.dto;

import java.time.Instant;

import com.gamelisto.usuarios_service.domain.usuario.Usuario;

public record UsuarioDTO(
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
    public static UsuarioDTO from(Usuario usuario) {
        return new UsuarioDTO(
            usuario.getId().value().toString(),
            usuario.getUsername().value(),
            usuario.getEmail().value(),
            usuario.getAvatar().url(),
            usuario.getCreatedAt(),
            usuario.getUpdatedAt(),
            usuario.getRole().name(),
            usuario.getLanguage().name(),
            usuario.isNotificationsActive(),
            usuario.getStatus().name(),
            usuario.getDiscordUserId().value(),
            usuario.getDiscordUsername().value(),
            usuario.getDiscordLinkedAt(),
            usuario.isDiscordConsent()
        );
    }
}
