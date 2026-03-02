package com.gamelisto.publicaciones.domain;

import com.gamelisto.publicaciones.domain.exceptions.DomainException;
import com.gamelisto.publicaciones.domain.vo.PeticionId;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PeticionUnion {
  private final PeticionId id;
  private final PublicacionId publicacionId;
  private final UsuarioId usuarioId;
  private EstadoSolicitud estadoSolicitud;

  private PeticionUnion(
      PeticionId id,
      PublicacionId publicacionId,
      UsuarioId usuarioId,
      EstadoSolicitud estadoSolicitud) {
    this.id = id;
    this.publicacionId = publicacionId;
    this.usuarioId = usuarioId;
    this.estadoSolicitud = estadoSolicitud;
  }

  public static PeticionUnion create(
      PublicacionId publicacionId, UsuarioId usuarioId, EstadoSolicitud estadoSolicitud) {
    return new PeticionUnion(
        PeticionId.of(java.util.UUID.randomUUID()), publicacionId, usuarioId, estadoSolicitud);
  }

  public static PeticionUnion reconstitute(
      PeticionId id,
      PublicacionId publicacionId,
      UsuarioId usuarioId,
      EstadoSolicitud estadoSolicitud) {
    return new PeticionUnion(id, publicacionId, usuarioId, estadoSolicitud);
  }

  public void cambiarEstado(EstadoSolicitud nuevo) {
    if (nuevo == null) throw new DomainException("Estado requerido");
    this.estadoSolicitud = nuevo;
  }
}
