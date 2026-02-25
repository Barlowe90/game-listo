package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.EditarListaGameCommand;

public record EditarListaGameRequest(String nombre) {

  public EditarListaGameCommand toCommand(String userId, String listaId) {
    return new EditarListaGameCommand(userId, listaId, nombre);
  }
}
