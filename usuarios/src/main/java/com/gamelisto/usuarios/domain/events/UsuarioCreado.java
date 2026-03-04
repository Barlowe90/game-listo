package com.gamelisto.usuarios.domain.events;

import java.time.Instant;

public record UsuarioCreado(
    String usuarioId,
    String username,
    String email,
    String avatar,
    String role,
    String language,
    String status,
    String discordUserId,
    String discordUsername,
    Instant occurredOn) {
  public static UsuarioCreado of(
      String usuarioId,
      String username,
      String email,
      String avatar,
      String role,
      String language,
      String status,
      String discordUserId,
      String discordUsername) {
    return new UsuarioCreado(
        usuarioId,
        username,
        email,
        avatar,
        role,
        language,
        status,
        discordUserId,
        discordUsername,
        Instant.now());
  }
}
