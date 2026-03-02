package com.gamelisto.publicaciones.infraestructure.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface UsuarioRefRepository extends MongoRepository<UsuarioRefDocument, UUID> {}
