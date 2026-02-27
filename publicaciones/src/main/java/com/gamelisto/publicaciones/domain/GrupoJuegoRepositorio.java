package com.gamelisto.publicaciones.domain;

import java.util.Optional;
import java.util.UUID;

public interface GrupoJuegoRepositorio {
  GrupoJuego save(GrupoJuego grupoJuego);

  Optional<GrupoJuego> findById(UUID id);

  Optional<GrupoJuego> findByPublicacionId(UUID publicacionId);

  void delete(GrupoJuego grupoJuego);
}
