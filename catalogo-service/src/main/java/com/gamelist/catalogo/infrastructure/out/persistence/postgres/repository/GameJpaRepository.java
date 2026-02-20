package com.gamelist.catalogo.infrastructure.out.persistence.postgres.repository;

import com.gamelist.catalogo.infrastructure.out.persistence.postgres.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameJpaRepository extends JpaRepository<GameEntity, Long> {

  @Query("SELECT COALESCE(MAX(g.id), 0) FROM GameEntity g")
  long findMaxId();
}
