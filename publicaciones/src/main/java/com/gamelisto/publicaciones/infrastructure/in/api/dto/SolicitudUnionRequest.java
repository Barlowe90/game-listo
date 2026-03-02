package com.gamelisto.publicaciones.infrastructure.in.api.dto;

import com.gamelisto.publicaciones.application.usecases.SolicitudUnionCommand;

import java.util.UUID;

public record SolicitudUnionRequest(String estadoSolicitud) {

  public SolicitudUnionCommand toCommand(UUID peticionUnionId, UUID userId) {
    return new SolicitudUnionCommand(peticionUnionId, userId, estadoSolicitud);
  }
}
