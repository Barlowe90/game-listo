package com.gamelisto.publicaciones.domain;

import com.gamelisto.publicaciones.domain.exceptions.DomainException;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class PeticionUnion {
  private final UUID id;
  private final UUID publicacionId;
  private final UUID usuarioId;
  private EstadoSolicitud estadoSolicitud;

  private PeticionUnion(
      UUID id, UUID publicacionId, UUID usuarioId, EstadoSolicitud estadoSolicitud) {
    this.id = id;
    this.publicacionId = publicacionId;
    this.usuarioId = usuarioId;
    this.estadoSolicitud = estadoSolicitud;
  }

  public static PeticionUnion create(
      UUID publicacionId, UUID usuarioId, EstadoSolicitud estadoSolicitud) {
    return new PeticionUnion(UUID.randomUUID(), publicacionId, usuarioId, estadoSolicitud);
  }

  public static PeticionUnion reconstitute(
      UUID id, UUID publicacionId, UUID usuarioId, EstadoSolicitud estadoSolicitud) {
    return new PeticionUnion(id, publicacionId, usuarioId, estadoSolicitud);
  }

  public void cambiarEstado(EstadoSolicitud nuevo) {
    if (nuevo == null) throw new DomainException("Estado requerido");
    this.estadoSolicitud = nuevo;
  }
}
