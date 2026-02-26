package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.CrearListaGameCommand;

public record CrearListaGameRequest(String nombre, String tipo) {

  public CrearListaGameCommand toCommand(String userId) {
    return new CrearListaGameCommand(userId, nombre, tipo);
  }
}
