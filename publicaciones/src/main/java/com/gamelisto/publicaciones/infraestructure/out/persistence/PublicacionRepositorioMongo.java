package com.gamelisto.publicaciones.infraestructure.out.persistence;

import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import com.gamelisto.publicaciones.domain.vo.GameId;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
  public Optional<Publicacion> findById(PublicacionId publicacionId) {
    return mongoRepository.findById(publicacionId.value()).map(mapper::toDomain);
  }

  @Override
  public List<Publicacion> findByAutorId(UsuarioId autorId) {
    return mongoRepository.findByAutorId(autorId.value()).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Publicacion> findByGameId(GameId gameId) {
    return mongoRepository.findByGameId(gameId.value()).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Publicacion> findAll() {
    return mongoRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public void deleteById(PublicacionId publicacionId) {
    mongoRepository.deleteById(publicacionId.value());
  }
}
