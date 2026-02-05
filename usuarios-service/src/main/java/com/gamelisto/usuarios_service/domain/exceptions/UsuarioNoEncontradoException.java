package com.gamelisto.usuarios_service.domain.exceptions;

import lombok.Getter;

@Getter
public class UsuarioNoEncontradoException extends RuntimeException {

  private final String usuarioId;

  public UsuarioNoEncontradoException(String usuarioId) {
    super(String.format("Usuario con ID '%s' no encontrado", usuarioId));
    this.usuarioId = usuarioId;
  }
}
