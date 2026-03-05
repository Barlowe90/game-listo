package com.gamelisto.usuarios.domain.events;

public record UsuarioCreado(
    String usuarioId,
    String username,
    String email,
    String avatar,
    String role,
    String language,
    String status,
    String discordUserId,
    String discordUsername) {
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
        usuarioId, username, email, avatar, role, language, status, discordUserId, discordUsername);
  }
}
