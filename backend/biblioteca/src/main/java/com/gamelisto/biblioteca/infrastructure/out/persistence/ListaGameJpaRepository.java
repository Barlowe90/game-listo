package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.NombreListaGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListaGameJpaRepository extends JpaRepository<ListaGameEntity, UUID> {
  List<ListaGameEntity> findByUsuarioRef_Id(UUID usuarioRefId);
}
