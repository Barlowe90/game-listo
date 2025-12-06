package com.gamelisto.usuarios_service.application.dto;

import java.time.Instant;

import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios_service.domain.usuario.Idioma;
import com.gamelisto.usuarios_service.domain.usuario.Rol;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;

/**
 * Para enviar la info de un usuario entre capas se usa esto.
 */
public record UsuarioDTO(
    String id,
    String username,
    String email,
    String avatar,
    Instant createdAt,
    Instant updatedAt,
    Rol role,
    Idioma language,
    boolean notificationsActive,
    EstadoUsuario status,
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
            usuario.getRole(),
            usuario.getLanguage(),
            usuario.isNotificationsActive(),
            usuario.getStatus(),
            usuario.getDiscordUserId().value(),
            usuario.getDiscordUsername().value(),
            usuario.getDiscordLinkedAt(),
            usuario.isDiscordConsent()
        );
    }
}
