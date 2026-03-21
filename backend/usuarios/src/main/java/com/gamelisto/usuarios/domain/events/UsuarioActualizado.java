package com.gamelisto.usuarios.domain.events;

public record UsuarioActualizado(
    String usuarioId,
    String username,
    String avatar,
    String discordUserId,
    String discordUsername) {
  public static UsuarioActualizado of(String usuarioId, String username, String avatar) {
    return new UsuarioActualizado(usuarioId, username, avatar, null, null);
  }

  public static UsuarioActualizado of(
      String usuarioId,
      String username,
      String avatar,
      String discordUserId,
      String discordUsername) {
    return new UsuarioActualizado(usuarioId, username, avatar, discordUserId, discordUsername);
  }
}
