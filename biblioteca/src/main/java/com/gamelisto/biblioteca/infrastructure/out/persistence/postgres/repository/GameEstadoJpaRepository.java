package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.repository;

import com.gamelisto.biblioteca.domain.gameestado.Estado;
import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.GameEstadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameEstadoJpaRepository extends JpaRepository<GameEstadoEntity, UUID> {

  Optional<GameEstadoEntity> findById(UUID id);

  Optional<GameEstadoEntity> findByEstado(Estado estado);
}
