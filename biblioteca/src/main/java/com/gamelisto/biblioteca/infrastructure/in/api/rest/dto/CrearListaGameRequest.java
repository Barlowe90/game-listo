package com.gamelisto.biblioteca.infrastructure.in.api.rest.dto;

import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameCommand;

public record CrearListaGameRequest(String usuarioRefId, String nombre, String tipo) {

  public CrearListaGameCommand toCommand() {
    return new CrearListaGameCommand(usuarioRefId, nombre, tipo);
  }
}
