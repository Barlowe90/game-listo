package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.dto.VincularDiscordCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.DiscordUserId;
import com.gamelisto.usuarios.domain.usuario.DiscordUsername;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VincularDiscordUseCase implements VincularDiscordHandle {

  private final RepositorioUsuarios repositorioUsuarios;

  @Transactional
  public UsuarioResult execute(VincularDiscordCommand command) {

    UsuarioId usuarioId = UsuarioId.of(command.usuarioId());
    Usuario usuario =
        repositorioUsuarios
            .findById(usuarioId)
            .orElseThrow(
                () ->
                    new ApplicationException(
                        "Usuario no encontrado con ID: " + command.usuarioId()));

    DiscordUserId discordUserId = DiscordUserId.of(command.discordUserId());
    repositorioUsuarios
        .findByDiscordUserId(discordUserId)
        .ifPresent(
            existingUser -> {
              if (!existingUser.getId().equals(usuarioId)) {
                throw new ApplicationException(
                    "La cuenta de Discord con ID '"
                        + command.discordUserId()
                        + "' ya está vinculada a otro usuario");
              }
            });

    DiscordUsername discordUsername = DiscordUsername.of(command.discordUsername());
    usuario.linkDiscord(discordUserId, discordUsername);

    Usuario usuarioActualizado = repositorioUsuarios.save(usuario);

    return UsuarioResult.from(usuarioActualizado);
  }
}
