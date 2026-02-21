package com.gamelist.catalogo.domain.repositories;

import com.gamelist.catalogo.domain.game.Game;
import com.gamelist.catalogo.domain.game.GameId;

import java.util.List;
import java.util.Optional;

public interface RepositorioGame {

  Game save(Game game);

  Optional<Game> findById(GameId id);

  List<Game> findAll();

  long findMaxId();
}
