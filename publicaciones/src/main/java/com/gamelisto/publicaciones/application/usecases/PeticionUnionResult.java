package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.PeticionUnion;

public record PeticionUnionResult(
    String id, String publicacionId, String usuarioId, String estadoSolicitud) {

  public static PeticionUnionResult from(PeticionUnion peticionUnion) {
    return new PeticionUnionResult(
        peticionUnion.getId().toString(),
        peticionUnion.getPublicacionId().toString(),
        peticionUnion.getUsuarioId().toString(),
        peticionUnion.getEstadoSolicitud().toString());
  }
}
