package com.gamelisto.biblioteca.infrastructure.in.api.rest.dto;

import com.gamelisto.biblioteca.application.usecase.editarlistagame.EditarListaGameCommand;

public record EditarListaGameRequest(String idLista, String nombre) {

  public EditarListaGameCommand toCommand(String idLista) {
    return new EditarListaGameCommand(this.idLista, nombre);
  }
}
