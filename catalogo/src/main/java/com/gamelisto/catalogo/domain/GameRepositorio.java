package com.gamelisto.catalogo.domain;

import java.util.List;
import java.util.Optional;

public interface GameRepositorio {

  Game save(Game game);

  Optional<Game> findById(GameId id);

  List<Game> findAll();

  long findMaxId();
}
