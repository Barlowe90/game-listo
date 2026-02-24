package com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.repository;

import com.gamelisto.biblioteca.infrastructure.out.persistence.postgres.entity.ListaGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ListaGameJpaRepository extends JpaRepository<ListaGameEntity, UUID> {
  List<ListaGameEntity> findByUsuarioRef_Id(UUID usuarioRefId);
}
