package com.gamelisto.biblioteca.infrastructure.in.api.rest.dto;

import com.gamelisto.biblioteca.application.usecase.editarlistagame.EditarListaGameCommand;

public record EditarListaGameRequest(String listaId, String nombre) {

  public EditarListaGameCommand toCommand(String listaId) {
    return new EditarListaGameCommand(this.listaId, nombre);
  }
}
