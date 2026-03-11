package com.gamelisto.publicaciones.domain;

import com.gamelisto.publicaciones.domain.vo.GrupoId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import java.util.List;

public interface GrupoJuegoUsuarioRepositorio {
  GrupoJuegoUsuario save(GrupoJuegoUsuario grupoJuegoUsuario);

  List<GrupoJuegoUsuario> findByGrupoId(GrupoId grupoId);

  boolean existsByGrupoIdAndUsuarioId(GrupoId grupoId, UsuarioId usuarioId);

  void deleteByGrupoIdAndUsuarioId(GrupoId grupoId, UsuarioId usuarioId);
}
