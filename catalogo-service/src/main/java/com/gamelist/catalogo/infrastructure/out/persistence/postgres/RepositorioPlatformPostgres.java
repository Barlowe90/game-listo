package com.gamelist.catalogo.infrastructure.out.persistence.postgres;

import com.gamelist.catalogo.domain.platform.Platform;
import com.gamelist.catalogo.domain.repositories.RepositorioPlataforma;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RepositorioPlatformPostgres implements RepositorioPlataforma {

  private final PlatformJpaRepository jpaRepository;
  private final PlatformMapper mapper;

  @Override
  @Transactional
  public Platform save(Platform platform) {
    PlatformEntity entity = mapper.toEntity(platform);
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
  public List<Platform> saveAll(List<Platform> platforms) {
    List<PlatformEntity> entities = platforms.stream().map(mapper::toEntity).toList();
    List<PlatformEntity> savedEntities = jpaRepository.saveAll(entities);
    return savedEntities.stream().map(mapper::toDomain).toList();
  }
}
