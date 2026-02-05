package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObtenerPerfilAutenticadoUseCase {

  private final RepositorioUsuarios repositorioUsuarios;

  public ObtenerPerfilAutenticadoUseCase(RepositorioUsuarios repositorioUsuarios) {
    this.repositorioUsuarios = repositorioUsuarios;
  }

  @Transactional(readOnly = true)
  public UsuarioDTO execute(String idUsuario) {

    UsuarioId id = UsuarioId.of(UUID.fromString(idUsuario));

    Usuario usuario =
        repositorioUsuarios
            .findById(id)
            .orElseThrow(
                () -> new UsuarioNoEncontradoException("Usuario no encontrado: " + idUsuario));

    return UsuarioDTO.from(usuario);
  }
}
