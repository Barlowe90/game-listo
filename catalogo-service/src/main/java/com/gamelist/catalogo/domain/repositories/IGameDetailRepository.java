package com.gamelist.catalogo_service.domain.repositories;

import com.gamelist.catalogo_service.domain.game.GameId;
import com.gamelist.catalogo_service.domain.gamedetail.GameDetail;

import java.util.Optional;

public interface IGameDetailRepository {
  GameDetail save(GameDetail gameDetail);

  Optional<GameDetail> findByGameId(GameId gameId);

  void deleteByGameId(GameId gameId);
}
