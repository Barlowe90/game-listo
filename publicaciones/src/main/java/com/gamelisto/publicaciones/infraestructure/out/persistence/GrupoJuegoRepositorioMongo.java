package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.GrupoJuego;
import com.gamelisto.publicaciones.domain.GrupoJuegoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

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
  public Optional<GrupoJuego> findById(UUID id) {
    return mongoRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<GrupoJuego> findByPublicacionId(UUID publicacionId) {
    return mongoRepository.findByPublicacionId(publicacionId).map(mapper::toDomain);
  }

  @Override
  public void delete(GrupoJuego grupoJuego) {
    mongoRepository.deleteById(grupoJuego.getId());
  }
}
