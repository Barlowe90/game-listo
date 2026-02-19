package com.gamelist.catalogo.infrastructure.out.persistence.postgres.adapter;

import com.gamelist.catalogo.domain.repositories.ISyncStateRepository;
import com.gamelist.catalogo.domain.syncstate.SyncKey;
import com.gamelist.catalogo.domain.syncstate.SyncState;
import com.gamelist.catalogo.infrastructure.out.persistence.postgres.entity.SyncStateEntity;
import com.gamelist.catalogo.infrastructure.out.persistence.postgres.mapper.SyncStateMapper;
import com.gamelist.catalogo.infrastructure.out.persistence.postgres.repository.SyncStateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SyncStateRepositoryPostgres implements ISyncStateRepository {

  private final SyncStateJpaRepository jpaRepository;
  private final SyncStateMapper mapper;

  @Override
  @Transactional
  public SyncState save(SyncState syncState) {
    SyncStateEntity entity;
    Optional<SyncStateEntity> existingEntity = jpaRepository.findById(syncState.getKey().name());

    if (existingEntity.isPresent()) {
      entity = existingEntity.get();
      mapper.updateEntity(syncState, entity);
    } else {
      entity = mapper.toEntity(syncState);
    }

    SyncStateEntity saved = jpaRepository.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<SyncState> findByKey(SyncKey key) {
    return jpaRepository.findById(key.name()).map(mapper::toDomain);
  }

  @Override
  @Transactional
  public void deleteByKey(SyncKey key) {
    jpaRepository.deleteById(key.name());
  }
}
