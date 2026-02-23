package com.gamelisto.biblioteca.domain.repositories;

import com.gamelisto.biblioteca.domain.usuario.UsuarioRef;
import java.util.Optional;
import java.util.UUID;

public interface RepositorioUsuariosRef {
  UsuarioRef save(UsuarioRef usuario);

  Optional<UsuarioRef> findById(UUID id);

  Optional<UsuarioRef> findByUsername(String username);

  void delete(UsuarioRef usuario);
}
