package com.gamelisto.usuarios.domain.events;

public record UsuarioActualizado(String usuarioId, String username, String avatar) {
  public static UsuarioActualizado of(String usuarioId, String username, String avatar) {
    return new UsuarioActualizado(usuarioId, username, avatar);
  }
}
