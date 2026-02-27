package com.gamelisto.publicaciones.domain;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class GrupoJuego {
  private final UUID id;
  private final UUID publicacionId;
  private final Instant fechaCreacion;

  private GrupoJuego(UUID id, UUID publicacionId, Instant fechaCreacion) {
    this.id = id;
    this.publicacionId = publicacionId;
    this.fechaCreacion = fechaCreacion;
  }

  public static GrupoJuego create(UUID publicacionId) {
    return new GrupoJuego(UUID.randomUUID(), publicacionId, Instant.now());
  }

  public static GrupoJuego reconstitute(UUID id, UUID publicacionId, Instant fechaCreacion) {
    return new GrupoJuego(id, publicacionId, fechaCreacion);
  }
}
