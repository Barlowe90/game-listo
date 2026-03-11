package com.gamelisto.publicaciones.domain;

import java.util.Optional;

public interface GameRefRepositorio {
  GameRef save(GameRef gameRef);

  Optional<GameRef> findById(Long id);
}
