package com.gamelisto.publicaciones.domain;

import com.gamelisto.publicaciones.domain.vo.GrupoId;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import java.util.Optional;

public interface GrupoJuegoRepositorio {
  GrupoJuego save(GrupoJuego grupoJuego);

  Optional<GrupoJuego> findById(GrupoId id);

  Optional<GrupoJuego> findByPublicacionId(PublicacionId publicacionId);

  void delete(GrupoJuego grupoJuego);
}
