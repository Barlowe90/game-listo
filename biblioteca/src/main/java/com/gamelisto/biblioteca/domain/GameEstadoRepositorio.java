package com.gamelisto.biblioteca.domain;

import java.util.Optional;

public interface GameEstadoRepositorio {

  GameEstado save(GameEstado gameEstado);

  Optional<GameEstado> findByUsuarioYGame(UsuarioId userId, GameId gameId);

  void deleteById(GameEstadoId id);
}
