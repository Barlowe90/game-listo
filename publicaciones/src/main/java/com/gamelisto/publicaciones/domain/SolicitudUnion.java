package com.gamelisto.publicaciones.domain;

import com.gamelisto.publicaciones.domain.exceptions.DomainException;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.SolicitudId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SolicitudUnion {
  private final SolicitudId id;
  private final PublicacionId publicacionId;
  private final UsuarioId usuarioId;
  private EstadoSolicitud estadoSolicitud;

  private SolicitudUnion(
      SolicitudId id,
      PublicacionId publicacionId,
      UsuarioId usuarioId,
      EstadoSolicitud estadoSolicitud) {
    this.id = id;
    this.publicacionId = publicacionId;
    this.usuarioId = usuarioId;
    this.estadoSolicitud = estadoSolicitud;
  }

  public static SolicitudUnion create(
      PublicacionId publicacionId, UsuarioId usuarioId, EstadoSolicitud estadoSolicitud) {
    return new SolicitudUnion(
        SolicitudId.of(java.util.UUID.randomUUID()), publicacionId, usuarioId, estadoSolicitud);
  }

  public static SolicitudUnion reconstitute(
      SolicitudId id,
      PublicacionId publicacionId,
      UsuarioId usuarioId,
      EstadoSolicitud estadoSolicitud) {
    return new SolicitudUnion(id, publicacionId, usuarioId, estadoSolicitud);
  }

  public void cambiarEstado(EstadoSolicitud nuevo) {
    if (nuevo == null) throw new DomainException("Estado requerido");
    this.estadoSolicitud = nuevo;
  }
}
