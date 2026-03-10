package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObtenerUsuarioPorIdUseCase implements ObtenerUsuarioPorIdHandle {

  private final RepositorioUsuarios repositorioUsuarios;

  @Transactional(readOnly = true)
  public UsuarioResult execute(String usuarioId) {
    UsuarioId id = UsuarioId.fromString(usuarioId);

    Usuario usuario =
        repositorioUsuarios
            .findById(id)
            .orElseThrow(
                () -> new ApplicationException("Usuario no encontrado con ID: " + usuarioId));

    return UsuarioResult.from(usuario);
  }
}
