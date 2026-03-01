package com.gamelisto.publicaciones.infraestructure.in.api.dto;

import com.gamelisto.publicaciones.application.usecases.PeticionUnionCommand;

import java.util.UUID;

public record PeticionUnionRequest(String estadoPeticion) {

  public PeticionUnionCommand toCommand(UUID peticionUnionId, UUID userId) {
    return new PeticionUnionCommand(peticionUnionId, userId, estadoPeticion);
  }
}
