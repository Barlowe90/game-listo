package com.gamelist.catalogo.domain.repositories;

import com.gamelist.catalogo.domain.game.GameId;
import com.gamelist.catalogo.domain.gamedetail.GameDetail;

import java.util.Optional;

public interface IGameDetailRepository {
  GameDetail save(GameDetail gameDetail);

  Optional<GameDetail> findByGameId(GameId gameId);
}
