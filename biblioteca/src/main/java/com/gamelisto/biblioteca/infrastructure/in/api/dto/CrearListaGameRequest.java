package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.CrearListaGameCommand;

public record CrearListaGameRequest(String usuarioRefId, String nombre, String tipo) {

  public CrearListaGameCommand toCommand() {
    return new CrearListaGameCommand(usuarioRefId, nombre, tipo);
  }
}
