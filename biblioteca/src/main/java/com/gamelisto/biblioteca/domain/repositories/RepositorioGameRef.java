package com.gamelisto.biblioteca.domain.repositories;

import com.gamelisto.biblioteca.domain.gameref.GameRef;

import java.util.Optional;
import java.util.UUID;

public interface RepositorioGameRef {
  GameRef save(GameRef gameRef);

  Optional<GameRef> findById(UUID id);

  Optional<GameRef> findByCatalogGameId(Long catalogGameId);
}
