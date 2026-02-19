package com.gamelist.catalogo.infrastructure.out.persistence.postgres.adapter;

import com.gamelist.catalogo.domain.catalog.PlatformId;
import com.gamelist.catalogo.domain.game.Game;
import com.gamelist.catalogo.domain.game.GameId;
import com.gamelist.catalogo.domain.repositories.IGameRepository;
import com.gamelist.catalogo.infrastructure.out.persistence.postgres.entity.GameEntity;
import com.gamelist.catalogo.infrastructure.out.persistence.postgres.entity.PlatformEntity;
import com.gamelist.catalogo.infrastructure.out.persistence.postgres.mapper.GameMapper;
import com.gamelist.catalogo.infrastructure.out.persistence.postgres.repository.GameJpaRepository;
import com.gamelist.catalogo.infrastructure.out.persistence.postgres.repository.PlatformJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GameRepositoryPostgres implements IGameRepository {

  private final GameJpaRepository jpaRepository;
  private final PlatformJpaRepository platformJpaRepository;
  private final GameMapper mapper;

  @Override
  @Transactional
  public Game save(Game game) {
    GameEntity entity;
    Optional<GameEntity> existingEntity = jpaRepository.findById(game.getId().value());

    if (existingEntity.isPresent()) {
      entity = existingEntity.get();
      mapper.updateEntity(game, entity);
    } else {
      entity = mapper.toEntity(game);
    }

    // Manejar relación M:N con plataformas
    Set<Long> platformIds =
        game.getPlatformIds().stream().map(PlatformId::value).collect(Collectors.toSet());

    Set<PlatformEntity> platforms = platformJpaRepository.findByIdIn(platformIds);
    entity.setPlatforms(platforms);

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
  public Optional<Game> findByName(String name) {
    return jpaRepository.findByName(name.trim()).map(mapper::toDomain);
  }

  @Override
  @Transactional
  public void deleteById(GameId id) {
    jpaRepository.deleteById(id.value());
  }
}
