package com.gamelisto.publicaciones.infrastructure.out.persistence;

import com.gamelisto.publicaciones.domain.GrupoJuegoUsuario;
import com.gamelisto.publicaciones.domain.GrupoJuegoUsuarioRepositorio;
import com.gamelisto.publicaciones.domain.vo.GrupoId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
  public List<GrupoJuegoUsuario> findByGrupoId(GrupoId grupoId) {
    return mongoRepository.findByGrupoId(grupoId.value()).stream().map(mapper::toDomain).toList();
  }

  @Override
  public boolean existsByGrupoIdAndUsuarioId(GrupoId grupoId, UsuarioId usuarioId) {
    return mongoRepository.existsByGrupoIdAndUsuarioId(grupoId.value(), usuarioId.value());
  }

  @Override
  public void deleteByGrupoIdAndUsuarioId(GrupoId grupoId, UsuarioId usuarioId) {
    mongoRepository.deleteByGrupoIdAndUsuarioId(grupoId.value(), usuarioId.value());
  }
}
