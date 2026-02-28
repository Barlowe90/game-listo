package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PublicacionRepositorioMongo implements PublicacionRepositorio {

  private final PublicacionRepository mongoRepository;
  private final PublicacionMapper mapper;

  @Override
  public Publicacion save(Publicacion publicacion) {
    return mapper.toDomain(mongoRepository.save(mapper.toDocument(publicacion)));
  }

  @Override
  public Optional<Publicacion> findById(UUID publicacionId) {
    return mongoRepository.findById(publicacionId).map(mapper::toDomain);
  }

  @Override
  public List<Publicacion> findByAutorId(UUID autorId) {
    return mongoRepository.findByAutorId(autorId).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Publicacion> findByGameId(Long gameId) {
    return mongoRepository.findByGameId(gameId).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Publicacion> findAll() {
    return mongoRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public void deleteById(UUID publicacionId) {
    mongoRepository.deleteById(publicacionId);
  }
}
