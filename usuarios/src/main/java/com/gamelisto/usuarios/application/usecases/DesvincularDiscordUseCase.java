package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DesvincularDiscordUseCase {

  private final RepositorioUsuarios repositorioUsuarios;

  public DesvincularDiscordUseCase(RepositorioUsuarios repositorioUsuarios) {
    this.repositorioUsuarios = repositorioUsuarios;
  }

  @Transactional
  public UsuarioDTO execute(String usuarioId) {

    UsuarioId id = UsuarioId.fromString(usuarioId);
    Usuario usuario =
        repositorioUsuarios
            .findById(id)
            .orElseThrow(
                () -> new ApplicationException("Usuario no encontrado con ID: " + usuarioId));

    usuario.unlinkDiscord();

    Usuario usuarioActualizado = repositorioUsuarios.save(usuario);

    return UsuarioDTO.from(usuarioActualizado);
  }
}
