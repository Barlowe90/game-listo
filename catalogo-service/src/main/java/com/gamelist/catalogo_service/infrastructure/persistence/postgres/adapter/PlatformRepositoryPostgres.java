package com.gamelist.catalogo_service.infrastructure.persistence.postgres.adapter;

import com.gamelist.catalogo_service.domain.catalog.Platform;
import com.gamelist.catalogo_service.domain.catalog.PlatformId;
import com.gamelist.catalogo_service.domain.repositories.IPlatformRepository;
import com.gamelist.catalogo_service.infrastructure.persistence.postgres.entity.PlatformEntity;
import com.gamelist.catalogo_service.infrastructure.persistence.postgres.mapper.PlatformMapper;
import com.gamelist.catalogo_service.infrastructure.persistence.postgres.repository.PlatformJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PlatformRepositoryPostgres implements IPlatformRepository {

  private final PlatformJpaRepository jpaRepository;
  private final PlatformMapper mapper;

  @Override
  @Transactional(readOnly = true)
  public Optional<Platform> findById(PlatformId id) {
    return jpaRepository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  @Transactional
  public Platform save(Platform platform) {
    PlatformEntity entity;
    Optional<PlatformEntity> existingEntity = jpaRepository.findById(platform.getId().value());

    if (existingEntity.isPresent()) {
      // Update existing
      entity = existingEntity.get();
      mapper.updateEntity(platform, entity);
    } else {
      // Create new
      entity = mapper.toEntity(platform);
    }

    PlatformEntity saved = jpaRepository.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Platform> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  @Transactional
  public void deleteById(PlatformId id) {
    jpaRepository.deleteById(id.value());
  }
}
