package com.gamelisto.biblioteca.domain.repositories;

import com.gamelisto.biblioteca.domain.gameestado.Estado;
import com.gamelisto.biblioteca.domain.gameestado.GameEstado;
import java.util.Optional;
import java.util.UUID;

public interface RepositorioGameEstado {

  GameEstado save(GameEstado gameEstado);

  Optional<GameEstado> findById(UUID id);

  Optional<GameEstado> findByEstado(Estado estado);
}
