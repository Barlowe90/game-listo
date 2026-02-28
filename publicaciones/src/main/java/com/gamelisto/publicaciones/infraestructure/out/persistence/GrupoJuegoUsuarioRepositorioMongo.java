package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.GrupoJuegoUsuario;
import com.gamelisto.publicaciones.domain.GrupoJuegoUsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GrupoJuegoUsuarioRepositorioMongo implements GrupoJuegoUsuarioRepositorio {

  private final GrupoJuegoUsuarioRepository mongoRepository;
  private final GrupoJuegoUsuarioMapper mapper;

  @Override
  public GrupoJuegoUsuario save(GrupoJuegoUsuario grupoJuegoUsuario) {
    return mapper.toDomain(mongoRepository.save(mapper.toDocument(grupoJuegoUsuario)));
  }

  @Override
  public List<GrupoJuegoUsuario> findByGrupoId(UUID grupoId) {
    return mongoRepository.findByGrupoId(grupoId).stream().map(mapper::toDomain).toList();
  }

  @Override
  public boolean existsByGrupoIdAndUsuarioId(UUID grupoId, UUID usuarioId) {
    return mongoRepository.existsByGrupoIdAndUsuarioId(grupoId, usuarioId);
  }

  @Override
  public void deleteByGrupoIdAndUsuarioId(UUID grupoId, UUID usuarioId) {
    mongoRepository.deleteByGrupoIdAndUsuarioId(grupoId, usuarioId);
  }
}
