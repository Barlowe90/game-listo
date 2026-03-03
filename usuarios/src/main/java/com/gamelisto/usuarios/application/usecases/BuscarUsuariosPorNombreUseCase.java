package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Username;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuscarUsuariosPorNombreUseCase {

  private final RepositorioUsuarios repositorioUsuarios;

  public BuscarUsuariosPorNombreUseCase(RepositorioUsuarios repositorioUsuarios) {
    this.repositorioUsuarios = repositorioUsuarios;
  }

  @Transactional(readOnly = true)
  public UsuarioDTO execute(String username) {
    Username u = Username.of(username);

    Usuario usuario =
        repositorioUsuarios
            .findByUsername(u)
            .orElseThrow(
                () -> new ApplicationException("Usuario no encontrado con username: " + username));

    return UsuarioDTO.from(usuario);
  }
}
