package com.gamelisto.usuarios.application.dto;

import com.gamelisto.usuarios.domain.usuario.Usuario;

public record UsuarioDTO(
    String id,
    String username,
    String email,
    String avatar,
    String role,
    String language,
    boolean notificationsActive,
    String status,
    String discordUserId,
    String discordUsername) {
  public static UsuarioDTO from(Usuario usuario) {
    return new UsuarioDTO(
        usuario.getId().value().toString(),
        usuario.getUsername().value(),
        usuario.getEmail().value(),
        usuario.getAvatar().url(),
        usuario.getRole().name(),
        usuario.getLanguage().name(),
        usuario.isNotificationsActive(),
        usuario.getStatus().name(),
        usuario.getDiscordUserId().value(),
        usuario.getDiscordUsername().value());
  }
}
