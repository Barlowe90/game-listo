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
  private EstadoPeticion estadoPeticion;

  private PeticionUnion(
      UUID id, UUID publicacionId, UUID usuarioId, EstadoPeticion estadoPeticion) {
    this.id = id;
    this.publicacionId = publicacionId;
    this.usuarioId = usuarioId;
    this.estadoPeticion = estadoPeticion;
  }

  public static PeticionUnion create(UUID publicacionId, UUID usuarioId) {
    return new PeticionUnion(
        UUID.randomUUID(), publicacionId, usuarioId, EstadoPeticion.SOLICITADA);
  }

  public static PeticionUnion reconstitute(
      UUID id, UUID publicacionId, UUID usuarioId, EstadoPeticion estadoPeticion) {
    return new PeticionUnion(id, publicacionId, usuarioId, estadoPeticion);
  }

  public void cambiarEstado(EstadoPeticion nuevo) {
    if (nuevo == null) throw new DomainException("Estado requerido");
    this.estadoPeticion = nuevo;
  }
}
