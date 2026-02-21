package com.gamelisto.biblioteca.domain.repositories;

import com.gamelisto.biblioteca.domain.game.GameRef;

import java.util.Optional;

public interface RepositorioGameRef {
  GameRef save(GameRef gameRef);

  Optional<GameRef> findById(String id);
}
