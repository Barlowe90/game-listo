package com.gamelisto.publicaciones.infraestructure.in.api.dto;

import com.gamelisto.publicaciones.application.usecases.UsuarioRefResult;

public record UsuarioRefResponse(String id, String username, String avatar) {
  public static UsuarioRefResponse from(UsuarioRefResult u) {
    return new UsuarioRefResponse(u.id(), u.username(), u.avatar());
  }
}
