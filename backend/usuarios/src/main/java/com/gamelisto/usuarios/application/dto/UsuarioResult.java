package com.gamelisto.usuarios.application.dto;

import com.gamelisto.usuarios.domain.usuario.Usuario;

public record UsuarioResult(
    String id,
    String username,
    String email,
    String avatar,
    String role,
    String status,
    String discordUserId) {
  public static UsuarioResult from(Usuario usuario) {
    return new UsuarioResult(
        usuario.getId().value().toString(),
        usuario.getUsername().value(),
        usuario.getEmail().value(),
        usuario.getAvatar().url(),
        usuario.getRole().name(),
        usuario.getStatus().name(),
        usuario.getDiscordUserId().value());
  }
}
