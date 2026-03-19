package com.gamelisto.catalogo.infrastructure.out.persistence.postgres;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface GameJpaRepository extends JpaRepository<GameEntity, Long> {

  @Query("SELECT COALESCE(MAX(g.id), 0) FROM GameEntity g")
  long findMaxId();

  @Query(
      value =
          """
          select distinct g
          from GameEntity g
          join g.platforms platform
          where lower(platform) in :platforms
          """,
      countQuery =
          """
          select count(distinct g.id)
          from GameEntity g
          join g.platforms platform
          where lower(platform) in :platforms
          """)
  Page<GameEntity> findPageByPlatforms(
      @Param("platforms") Collection<String> platforms, Pageable pageable);
}
