package com.gamelisto.publicaciones.domain;

import java.util.List;
import java.util.UUID;

public interface GrupoJuegoUsuarioRepositorio {
  GrupoJuegoUsuario save(GrupoJuegoUsuario grupoJuegoUsuario);

  List<GrupoJuegoUsuario> findByGrupoId(UUID grupoId);

  boolean existsByGrupoIdAndUsuarioId(UUID grupoId, UUID usuarioId); // no usada

  void deleteByGrupoIdAndUsuarioId(UUID grupoId, UUID usuarioId);
}
