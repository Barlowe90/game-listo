package com.gamelisto.publicaciones.infrastructure.in.api.dto;

import com.gamelisto.publicaciones.application.usecases.PeticionUnionCommand;

import java.util.UUID;

public record PeticionUnionRequest(String estadoSolicitud) {

  public PeticionUnionCommand toCommand(UUID peticionUnionId, UUID userId) {
    return new PeticionUnionCommand(peticionUnionId, userId, estadoSolicitud);
  }
}
