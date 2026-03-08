package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.EditarListaGameCommand;

import java.util.UUID;

public record EditarListaGameRequest(String nombre) {

  public EditarListaGameCommand toCommand(UUID userId, String listaId) {
    return new EditarListaGameCommand(userId, listaId, nombre);
  }
}
