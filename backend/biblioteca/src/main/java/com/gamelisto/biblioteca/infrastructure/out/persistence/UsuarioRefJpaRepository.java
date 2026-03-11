package com.gamelisto.biblioteca.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRefJpaRepository extends JpaRepository<UsuarioRefEntity, UUID> {

  Optional<UsuarioRefEntity> findByUsername(String username);
}
