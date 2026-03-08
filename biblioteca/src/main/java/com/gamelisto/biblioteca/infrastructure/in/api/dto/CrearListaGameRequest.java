package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.CrearListaGameCommand;

import java.util.UUID;

public record CrearListaGameRequest(String nombre, String tipo) {

  public CrearListaGameCommand toCommand(UUID userId) {
    return new CrearListaGameCommand(userId, nombre, tipo);
  }
}
