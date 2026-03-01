package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.UsuarioRef;

public record UsuarioRefResult(String id, String username, String avatar) {
  public static UsuarioRefResult from(UsuarioRef u) {
    return new UsuarioRefResult(u.getId().toString(), u.getUsername(), u.getAvatar());
  }
}
