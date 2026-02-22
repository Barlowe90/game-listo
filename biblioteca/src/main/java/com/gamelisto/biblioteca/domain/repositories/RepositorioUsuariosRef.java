package com.gamelisto.biblioteca.domain.repositories;

import com.gamelisto.biblioteca.domain.usuario.UsuarioRef;
import java.util.Optional;

public interface RepositorioUsuariosRef {
  UsuarioRef save(UsuarioRef usuario);

  Optional<UsuarioRef> finById(String id);

  Optional<UsuarioRef> finByUsername(String username);

  void delete(UsuarioRef id);
}
