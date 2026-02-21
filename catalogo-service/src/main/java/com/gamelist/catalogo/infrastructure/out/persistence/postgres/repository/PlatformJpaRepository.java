package com.gamelist.catalogo.infrastructure.out.persistence.postgres.repository;

import com.gamelist.catalogo.infrastructure.out.persistence.postgres.entity.PlatformEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformJpaRepository extends JpaRepository<PlatformEntity, Long> {}
