package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.CrearGameEstadoCommand;

import java.util.UUID;

public record CrearGameEstadoRequest(String estado) {

  public CrearGameEstadoCommand toCommand(UUID userId, String gameRefId) {
    return new CrearGameEstadoCommand(userId, gameRefId, estado);
  }
}
