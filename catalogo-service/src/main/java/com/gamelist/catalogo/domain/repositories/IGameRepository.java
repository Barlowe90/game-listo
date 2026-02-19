package com.gamelist.catalogo.domain.repositories;

import com.gamelist.catalogo.domain.game.Game;
import com.gamelist.catalogo.domain.game.GameId;

import java.util.Optional;

public interface IGameRepository {

  Game save(Game game);

  Optional<Game> findById(GameId id);

  Optional<Game> findByName(String name);

  void deleteById(GameId id);
}
