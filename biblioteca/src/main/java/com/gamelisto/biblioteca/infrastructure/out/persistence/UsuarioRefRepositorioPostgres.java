package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import com.gamelisto.biblioteca.domain.UsuariosRefRepositorio;
import com.gamelisto.biblioteca.domain.UsuarioRef;
import com.gamelisto.biblioteca.domain.UsuarioId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioRefRepositorioPostgres implements UsuariosRefRepositorio {

  private final UsuarioRefJpaRepository jpaRepository;
  private final UsuarioRefMapper mapper;
  private final ListaGameRepositorio listaGameRepositorio;

  public UsuarioRefRepositorioPostgres(
      UsuarioRefJpaRepository jpaRepository,
      UsuarioRefMapper mapper,
      ListaGameRepositorio listaGameRepositorio) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
    this.listaGameRepositorio = listaGameRepositorio;
  }

  @Override
  public UsuarioRef save(UsuarioRef usuario) {
    UsuarioRefEntity entity = mapper.toEntity(usuario);
    UsuarioRefEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Optional<UsuarioRef> findById(UsuarioId id) {
    return jpaRepository
        .findById(id.value())
        .map(mapper::toDomain)
        .map(this::cargarListasDelUsuario);
  }

  @Override
  public void deleteById(UsuarioId id) {
    jpaRepository.deleteById(id.value());
  }

  private UsuarioRef cargarListasDelUsuario(UsuarioRef usuarioRef) {
    listaGameRepositorio.findByUsuarioRefId(usuarioRef.getId()).forEach(usuarioRef::addNuevaLista);
    return usuarioRef;
  }
}
