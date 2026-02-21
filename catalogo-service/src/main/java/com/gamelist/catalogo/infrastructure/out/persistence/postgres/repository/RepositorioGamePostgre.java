package com.gamelist.catalogo.infrastructure.out.persistence.postgres.repository;

import com.gamelist.catalogo.domain.game.Game;
import com.gamelist.catalogo.domain.game.GameId;
import com.gamelist.catalogo.domain.repositories.RepositorioGame;
import com.gamelist.catalogo.infrastructure.out.persistence.postgres.entity.GameEntity;
import com.gamelist.catalogo.infrastructure.out.persistence.postgres.mapper.GameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RepositorioGamePostgre implements RepositorioGame {

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
