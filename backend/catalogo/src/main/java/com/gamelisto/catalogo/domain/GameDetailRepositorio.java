package com.gamelisto.catalogo.domain;

import java.util.Optional;

public interface GameDetailRepositorio {
  GameDetail save(GameDetail gameDetail);

  Optional<GameDetail> findByGameId(GameId gameId);
}
