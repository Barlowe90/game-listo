package com.gamelisto.publicaciones.domain;

import com.gamelisto.publicaciones.domain.vo.GameId;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import java.util.List;
import java.util.Optional;

public interface PublicacionRepositorio {
  Publicacion save(Publicacion publicacion);

  Optional<Publicacion> findById(PublicacionId publicacionId);

  List<Publicacion> findByAutorId(UsuarioId autorId);

  List<Publicacion> findByGameId(GameId gameId);

  List<Publicacion> findAll();

  void deleteById(PublicacionId publicacionId);
}
