package com.gamelisto.usuarios.domain.events;

public record UsuarioCreado(
    String usuarioId,
    String username,
    String email,
    String avatar,
    String role,
    String status,
    String discordUserId) {
  public static UsuarioCreado of(
      String usuarioId,
      String username,
      String email,
      String avatar,
      String role,
      String status,
      String discordUserId) {
    return new UsuarioCreado(
        usuarioId, username, email, avatar, role, status, discordUserId);
  }
}
