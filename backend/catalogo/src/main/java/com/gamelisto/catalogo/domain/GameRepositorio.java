package com.gamelisto.catalogo.domain;

import java.util.List;
import java.util.Optional;

public interface GameRepositorio {

  Game save(Game game);

  Optional<Game> findById(GameId id);

  PageResult<Game> findAll(int page, int size, List<String> platforms);

  PageResult<GameCardSummary> findSummaries(int page, int size, List<String> platforms);

  long findMaxId();
}
