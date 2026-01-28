package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObtenerUsuarioPorId {

  private final RepositorioUsuarios repositorioUsuarios;

  public ObtenerUsuarioPorId(RepositorioUsuarios repositorioUsuarios) {
    this.repositorioUsuarios = repositorioUsuarios;
  }

  @Transactional(readOnly = true)
  public UsuarioDTO execute(String usuarioId) {
    UsuarioId id = UsuarioId.fromString(usuarioId);

    Usuario usuario =
        repositorioUsuarios
            .findById(id)
            .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));

    return UsuarioDTO.from(usuario);
  }
}
