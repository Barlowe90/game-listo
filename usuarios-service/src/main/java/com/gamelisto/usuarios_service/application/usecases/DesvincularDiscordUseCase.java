package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
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
                () ->
                    new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + usuarioId));

    usuario.unlinkDiscord();

    Usuario usuarioActualizado = repositorioUsuarios.save(usuario);

    return UsuarioDTO.from(usuarioActualizado);
  }
}
