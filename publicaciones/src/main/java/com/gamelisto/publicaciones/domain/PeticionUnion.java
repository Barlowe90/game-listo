package com.gamelisto.publicaciones.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class PeticionUnion {
  private final UUID id;
  private final UUID publicacionId;
  private final UUID usuarioId;
  private final EstadoPeticion estadoPeticion;

  private PeticionUnion(
      UUID id, UUID publicacionId, UUID usuarioId, EstadoPeticion estadoPeticion) {
    this.id = id;
    this.publicacionId = publicacionId;
    this.usuarioId = usuarioId;
    this.estadoPeticion = estadoPeticion;
  }

  public static PeticionUnion create(
      UUID publicacionId, UUID usuarioId, EstadoPeticion estadoPeticion) {
    return new PeticionUnion(UUID.randomUUID(), publicacionId, usuarioId, estadoPeticion);
  }

  public static PeticionUnion reconstitute(
      UUID id, UUID publicacionId, UUID usuarioId, EstadoPeticion estadoPeticion) {
    return new PeticionUnion(id, publicacionId, usuarioId, estadoPeticion);
  }
}
