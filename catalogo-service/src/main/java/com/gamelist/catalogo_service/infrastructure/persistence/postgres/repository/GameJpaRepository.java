package com.gamelist.catalogo_service.infrastructure.persistence.postgres.repository;

import com.gamelist.catalogo_service.infrastructure.persistence.postgres.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameJpaRepository extends JpaRepository<GameEntity, Long> {
  Optional<GameEntity> findByName(String name);
}
