package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.CrearGameEstadoCommand;

public record CrearGameEstadoRequest(String estado) {

  public CrearGameEstadoCommand toCommand(String userId, String gameRefId) {
    return new CrearGameEstadoCommand(userId, gameRefId, estado);
  }
}
