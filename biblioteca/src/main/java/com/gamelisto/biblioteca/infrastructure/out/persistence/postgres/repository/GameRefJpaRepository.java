package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.repository;

import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.GameRefEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameRefJpaRepository extends JpaRepository<GameRefEntity, UUID> {
  Optional<GameRefEntity> findById(UUID id);

  Optional<GameRefEntity> findByCatalogGameId(Long catalogGameId);
}
