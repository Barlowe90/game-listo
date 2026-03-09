package com.gamelisto.biblioteca.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameEstadoJpaRepository extends JpaRepository<GameEstadoEntity, UUID> {

  Optional<GameEstadoEntity> findByUsuarioRef_IdAndGameRef_Id(UUID userId, Long gameId);

  List<GameEstadoEntity> findByGameRef_Id(Long gameId);
}
