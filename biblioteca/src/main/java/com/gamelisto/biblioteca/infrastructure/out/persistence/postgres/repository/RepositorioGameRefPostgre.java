package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.repository;

import com.gamelisto.biblioteca.domain.game.GameRef;
import com.gamelisto.biblioteca.domain.repositories.RepositorioGameRef;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.mapper.GameRefMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RepositorioGameRefPostgre implements RepositorioGameRef {

  private final GameRefJpaRepository jpaRepository;
  private final GameRefMapper mapper;

  public RepositorioGameRefPostgre(GameRefJpaRepository jpaRepository, GameRefMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public GameRef save(GameRef gameRef) {
    return null;
  }

  @Override
  public Optional<GameRef> findById(String id) {
    return Optional.empty();
  }
}
