package com.gamelisto.biblioteca.infrastructure.in.api.rest.dto;

import com.gamelisto.biblioteca.application.usecase.editarlistagame.EditarListaGameCommand;

public record EditarListaGameRequest(String nombre) {

  public EditarListaGameCommand toCommand(String userId, String listaId) {
    return new EditarListaGameCommand(userId, listaId, nombre);
  }
}
