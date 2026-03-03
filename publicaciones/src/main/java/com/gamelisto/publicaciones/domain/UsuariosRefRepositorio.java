package com.gamelisto.publicaciones.domain;

import java.util.Optional;
import java.util.UUID;

public interface UsuariosRefRepositorio {
  UsuarioRef save(UsuarioRef usuario);

  Optional<UsuarioRef> findById(UUID id);
}
