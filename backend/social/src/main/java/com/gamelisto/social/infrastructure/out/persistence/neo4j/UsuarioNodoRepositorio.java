package com.gamelisto.social.infrastructure.out.persistence.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsuarioNodoRepositorio extends Neo4jRepository<UsuarioNodo, UUID> {}
