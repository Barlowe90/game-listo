package com.gamelist.catalogo_service.domain.repositories;

import com.gamelist.catalogo_service.domain.game.GameId;
import com.gamelist.catalogo_service.domain.gamedetail.GameDetail;

import java.util.Optional;

public interface IGameDetailRepository {

  Optional<GameDetail> findByGameId(GameId gameId);

  GameDetail save(GameDetail gameDetail);

  void deleteByGameId(GameId gameId);
}
