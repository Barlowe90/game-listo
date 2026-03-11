package com.gamelisto.usuarios.application.usecases.auth;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObtenerPerfilAutenticadoUseCase implements ObtenerPerfilAutenticadoHandle {

  private final RepositorioUsuarios repositorioUsuarios;

  @Transactional(readOnly = true)
  public UsuarioResult execute(UUID idUsuario) {

    UsuarioId id = UsuarioId.of(idUsuario);

    Usuario usuario =
        repositorioUsuarios
            .findById(id)
            .orElseThrow(() -> new ApplicationException("Usuario no encontrado: " + idUsuario));

    return UsuarioResult.from(usuario);
  }
}
