package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.repository;

import com.gamelisto.biblioteca.domain.game.Estado;
import com.gamelisto.biblioteca.domain.game.GameEstado;
import com.gamelisto.biblioteca.domain.repositories.RepositorioGameEstado;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.mapper.GameEstadoMapper;

import java.util.Optional;

public class RepositorioGameEstadoPostgre implements RepositorioGameEstado {

  private final GameEstadoJpaRepository jpaRepository;
  private final GameEstadoMapper mapper;

  public RepositorioGameEstadoPostgre(
      GameEstadoJpaRepository jpaRepository, GameEstadoMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public GameEstado save(GameEstado gameEstado) {
    return null;
  }

  @Override
  public Optional<GameEstado> findById(String id) {
    return Optional.empty();
  }

  @Override
  public Optional<GameEstado> findByEstado(Estado estado) {
    return Optional.empty();
  }
}
