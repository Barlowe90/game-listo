package com.gamelisto.publicaciones.infraestructure.in.api.dto;

import com.gamelisto.publicaciones.application.usecases.PeticionUnionResult;

public record PeticionUnionResponse(
    String id, String publicacionId, String usuarioId, String estadoSolicitud) {

  public static PeticionUnionResponse from(PeticionUnionResult p) {
    return new PeticionUnionResponse(p.id(), p.publicacionId(), p.usuarioId(), p.estadoSolicitud());
  }
}
