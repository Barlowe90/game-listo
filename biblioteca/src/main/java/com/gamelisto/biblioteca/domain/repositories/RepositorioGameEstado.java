package com.gamelisto.biblioteca.domain.repositories;

import com.gamelisto.biblioteca.domain.game.Estado;
import com.gamelisto.biblioteca.domain.game.GameEstado;
import java.util.Optional;

public interface RepositorioGameEstado {

  GameEstado save(GameEstado gameEstado);

  Optional<GameEstado> findById(String id);

  Optional<GameEstado> findByEstado(Estado estado);
}
