package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.GrupoJuego;
import com.gamelisto.publicaciones.domain.GrupoJuegoRepositorio;
import com.gamelisto.publicaciones.domain.vo.GrupoId;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GrupoJuegoRepositorioMongo implements GrupoJuegoRepositorio {

  private final GrupoJuegoRepository mongoRepository;
  private final GrupoJuegoMapper mapper;

  @Override
  public GrupoJuego save(GrupoJuego grupoJuego) {
    return mapper.toDomain(mongoRepository.save(mapper.toDocument(grupoJuego)));
  }

  @Override
  public Optional<GrupoJuego> findById(GrupoId id) {
    return mongoRepository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  public Optional<GrupoJuego> findByPublicacionId(PublicacionId publicacionId) {
    return mongoRepository.findByPublicacionId(publicacionId.value()).map(mapper::toDomain);
  }

  @Override
  public void delete(GrupoJuego grupoJuego) {
    mongoRepository.deleteById(grupoJuego.getId().value());
  }
}
