package com.gamelisto.biblioteca.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRefJpaRepository extends JpaRepository<GameRefEntity, Long> {
  Optional<GameRefEntity> findById(Long id);
}
