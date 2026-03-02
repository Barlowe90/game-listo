package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.SolicitudUnion;

public record SolicitudUnionResult(
    String id, String publicacionId, String usuarioId, String estadoSolicitud) {

  public static SolicitudUnionResult from(SolicitudUnion solicitudUnion) {
    return new SolicitudUnionResult(
        solicitudUnion.getId().value().toString(),
        solicitudUnion.getPublicacionId().value().toString(),
        solicitudUnion.getUsuarioId().value().toString(),
        solicitudUnion.getEstadoSolicitud().toString());
  }
}
