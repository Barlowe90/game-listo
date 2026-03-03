package com.gamelisto.publicaciones.infrastructure.in.api.dto;

import com.gamelisto.publicaciones.application.usecases.SolicitudUnionResult;

public record SolicitudUnionResponse(
    String id, String publicacionId, String usuarioId, String estadoSolicitud) {

  public static SolicitudUnionResponse from(SolicitudUnionResult p) {
    return new SolicitudUnionResponse(
        p.id(), p.publicacionId(), p.usuarioId(), p.estadoSolicitud());
  }
}
