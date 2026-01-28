package com.gamelisto.usuarios_service.application.dto;

import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import java.time.Instant;

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
    Instant discordLinkedAt) {
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
        usuario.getDiscordLinkedAt());
  }
}
