package com.gamelisto.biblioteca.domain;

import java.util.Optional;
import java.util.UUID;

public interface GameEstadoRepositorio {

  GameEstado save(GameEstado gameEstado);

  Optional<GameEstado> findByUsuarioYGame(UUID userId, Long gameId);

  void deleteById(UUID id);
}
