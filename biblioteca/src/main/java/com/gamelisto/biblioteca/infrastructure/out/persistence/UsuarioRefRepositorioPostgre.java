package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import com.gamelisto.biblioteca.domain.UsuariosRefRepositorio;
import com.gamelisto.biblioteca.domain.UsuarioRef;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UsuarioRefRepositorioPostgre implements UsuariosRefRepositorio {

  private final UsuarioRefJpaRepository jpaRepository;
  private final UsuarioRefMapper mapper;
  private final ListaGameRepositorio listaGameRepositorio;

  public UsuarioRefRepositorioPostgre(
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
  public Optional<UsuarioRef> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain).map(this::cargarListasDelUsuario);
  }

  private UsuarioRef cargarListasDelUsuario(UsuarioRef usuarioRef) {
    listaGameRepositorio.findByUsuarioRefId(usuarioRef.getId()).forEach(usuarioRef::addNuevaLista);
    return usuarioRef;
  }
}
