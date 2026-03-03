package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
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
            .orElseThrow(() -> new ApplicationException("Usuario no encontrado: " + idUsuario));

    return UsuarioDTO.from(usuario);
  }
}
