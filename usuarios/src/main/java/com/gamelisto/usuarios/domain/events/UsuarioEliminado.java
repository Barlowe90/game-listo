package com.gamelisto.usuarios.domain.events;

public record UsuarioEliminado(String usuarioId) {
  public static UsuarioEliminado of(String usuarioId) {
    return new UsuarioEliminado(usuarioId);
  }
}
