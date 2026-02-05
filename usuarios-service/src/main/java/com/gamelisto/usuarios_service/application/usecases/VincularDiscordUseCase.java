package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.application.dto.VincularDiscordCommand;
import com.gamelisto.usuarios_service.domain.exceptions.DiscordYaVinculadoException;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.DiscordUserId;
import com.gamelisto.usuarios_service.domain.usuario.DiscordUsername;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VincularDiscordUseCase {

  private final RepositorioUsuarios repositorioUsuarios;

  public VincularDiscordUseCase(RepositorioUsuarios repositorioUsuarios) {
    this.repositorioUsuarios = repositorioUsuarios;
  }

  @Transactional
  public UsuarioDTO execute(VincularDiscordCommand command) {

    UsuarioId usuarioId = UsuarioId.fromString(command.usuarioId());
    Usuario usuario =
        repositorioUsuarios
            .findById(usuarioId)
            .orElseThrow(
                () ->
                    new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + command.usuarioId()));

    DiscordUserId discordUserId = DiscordUserId.of(command.discordUserId());
    repositorioUsuarios
        .findByDiscordUserId(discordUserId)
        .ifPresent(
            existingUser -> {
              if (!existingUser.getId().equals(usuarioId)) {
                throw new DiscordYaVinculadoException(command.discordUserId());
              }
            });

    DiscordUsername discordUsername = DiscordUsername.of(command.discordUsername());
    usuario.linkDiscord(discordUserId, discordUsername);

    Usuario usuarioActualizado = repositorioUsuarios.save(usuario);

    return UsuarioDTO.from(usuarioActualizado);
  }
}
