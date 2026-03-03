package com.gamelisto.publicaciones.domain;

import com.gamelisto.publicaciones.domain.vo.GrupoJuegoUsuarioId;
import com.gamelisto.publicaciones.domain.vo.GrupoId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GrupoJuegoUsuario {
  private final GrupoJuegoUsuarioId id;
  private final GrupoId grupoId;
  private final UsuarioId usuarioId;

  private GrupoJuegoUsuario(GrupoJuegoUsuarioId id, GrupoId grupoId, UsuarioId usuarioId) {
    this.id = id;
    this.grupoId = grupoId;
    this.usuarioId = usuarioId;
  }

  public static GrupoJuegoUsuario create(GrupoId grupoId, UsuarioId usuarioId) {
    return new GrupoJuegoUsuario(
        GrupoJuegoUsuarioId.of(java.util.UUID.randomUUID()), grupoId, usuarioId);
  }

  public static GrupoJuegoUsuario reconstitute(
      GrupoJuegoUsuarioId id, GrupoId grupoId, UsuarioId usuarioId) {
    return new GrupoJuegoUsuario(id, grupoId, usuarioId);
  }
}
