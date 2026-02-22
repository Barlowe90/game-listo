package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.repository;

import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.UsuarioRefEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRefJpaRepository extends JpaRepository<UsuarioRefEntity, UUID> {
  Optional<UsuarioRefEntity> finById(String id);

  Optional<UsuarioRefEntity> finByUsername(String username);
}
