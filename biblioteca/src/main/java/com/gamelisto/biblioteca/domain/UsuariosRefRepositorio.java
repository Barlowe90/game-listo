package com.gamelisto.biblioteca.domain;

import java.util.Optional;

public interface UsuariosRefRepositorio {
  UsuarioRef save(UsuarioRef usuario);

  Optional<UsuarioRef> findById(UsuarioId id);
}
