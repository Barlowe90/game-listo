package com.gamelist.catalogo_service.infrastructure.persistence.postgres.repository;

import com.gamelist.catalogo_service.infrastructure.persistence.postgres.entity.PlatformEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PlatformJpaRepository extends JpaRepository<PlatformEntity, Long> {

  Set<PlatformEntity> findByIdIn(Set<Long> ids);
}
