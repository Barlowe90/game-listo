package com.gamelisto.catalogo.infrastructure.out.persistence.postgres;

import com.gamelisto.catalogo.domain.Game;
import com.gamelisto.catalogo.domain.GameId;
import com.gamelisto.catalogo.domain.GameRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameRepositorioPostgre implements GameRepositorio {

  private final GameJpaRepository jpaRepository;
  private final GameMapper mapper;

  @Override
  @Transactional
  public Game save(Game game) {
    GameEntity entity = mapper.toEntity(game);
    GameEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Game> findById(GameId id) {
    return jpaRepository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Game> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public long findMaxId() {
    return jpaRepository.findMaxId();
  }
}
