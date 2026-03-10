package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Username;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuscarUsuariosPorNombreUseCase implements BuscarUsuariosPorNombreHandle {

  private final RepositorioUsuarios repositorioUsuarios;

  @Transactional(readOnly = true)
  public UsuarioResult execute(String username) {
    Username u = Username.of(username);

    Usuario usuario =
        repositorioUsuarios
            .findByUsername(u)
            .orElseThrow(
                () -> new ApplicationException("Usuario no encontrado con username: " + username));

    return UsuarioResult.from(usuario);
  }
}
