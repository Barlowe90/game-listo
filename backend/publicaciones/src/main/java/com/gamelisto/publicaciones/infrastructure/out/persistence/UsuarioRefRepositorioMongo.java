package com.gamelisto.publicaciones.infrastructure.out.persistence;

import java.util.Optional;
import java.util.UUID;

import com.gamelisto.publicaciones.domain.UsuarioRef;
import com.gamelisto.publicaciones.domain.UsuariosRefRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UsuarioRefRepositorioMongo implements UsuariosRefRepositorio {

  private final UsuarioRefRepository mongoRepository;
  private final UsuarioRefMapper mapper;

  @Override
  public UsuarioRef save(UsuarioRef usuario) {
    return mapper.toDomain(mongoRepository.save(mapper.toEntity(usuario)));
  }

  @Override
  public Optional<UsuarioRef> findById(UUID id) {
    return mongoRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public void deleteById(UUID usuarioId) {
    mongoRepository.deleteById(usuarioId);
  }
}
