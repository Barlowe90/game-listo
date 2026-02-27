package com.gamelisto.publicaciones.domain;

import java.util.UUID;

public class GrupoJuegoUsuario {
  private final UUID id;
  private final UUID grupoId;
  private final UUID usuarioId;

  private GrupoJuegoUsuario(UUID id, UUID grupoId, UUID usuarioId) {
    this.id = id;
    this.grupoId = grupoId;
    this.usuarioId = usuarioId;
  }

  public static GrupoJuegoUsuario create(UUID grupoId, UUID usuarioId) {
    return new GrupoJuegoUsuario(UUID.randomUUID(), grupoId, usuarioId);
  }

  public static GrupoJuegoUsuario create(UUID id, UUID grupoId, UUID usuarioId) {
    return new GrupoJuegoUsuario(id, grupoId, usuarioId);
  }
}
