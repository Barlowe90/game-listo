package com.gamelisto.usuarios.domain.events;

public record UsuarioActualizado(
    String usuarioId, String username, String avatar, String discordUserId) {
  public static UsuarioActualizado of(String usuarioId, String username, String avatar) {
    return new UsuarioActualizado(usuarioId, username, avatar, null);
  }

  public static UsuarioActualizado of(
      String usuarioId, String username, String avatar, String discordUserId) {
    return new UsuarioActualizado(usuarioId, username, avatar, discordUserId);
  }
}
