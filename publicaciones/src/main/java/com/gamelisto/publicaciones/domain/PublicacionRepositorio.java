package com.gamelisto.publicaciones.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PublicacionRepositorio {
  Publicacion save(Publicacion publicacion);

  Optional<Publicacion> findById(UUID publicacionId);

  List<Publicacion> findByAutorId(UUID autorId);

  List<Publicacion> findByGameId(Long gameId);

  List<Publicacion> findAll();

  void deleteById(UUID publicacionId);
}
