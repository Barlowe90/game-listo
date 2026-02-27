package com.gamelisto.publicaciones.domain;

import java.util.List;
import java.util.UUID;

public interface GrupoJuegoUsuarioRepositorio {
  GrupoJuegoUsuario save(GrupoJuegoUsuario rel);

  List<GrupoJuegoUsuario> findByGrupoId(UUID grupoId);

  boolean existsByGrupoIdAndUsuarioId(UUID grupoId, UUID usuarioId);

  void deleteByGrupoIdAndUsuarioId(UUID grupoId, UUID usuarioId);
}
