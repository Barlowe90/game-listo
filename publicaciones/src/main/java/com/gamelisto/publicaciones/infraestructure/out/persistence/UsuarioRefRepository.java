package com.gamelisto.publicaciones.infraestructure.out.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRefRepository extends JpaRepository<UsuarioRefDocument, UUID> {}
