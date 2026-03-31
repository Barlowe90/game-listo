package com.gamelisto.usuarios.application.usecases.discord;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.dto.VincularDiscordCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.events.UsuarioActualizado;
import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.DiscordUserId;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class VincularDiscordUseCase implements VincularDiscordHandle {

  private final RepositorioUsuarios repositorioUsuarios;
  private final IUsuarioPublisher usuarioPublisher;

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

    usuario.linkDiscord(discordUserId);

    Usuario usuarioActualizado = repositorioUsuarios.save(usuario);
    publicarAfterCommit(() -> publicarUsuarioActualizado(usuarioActualizado));

    return UsuarioResult.from(usuarioActualizado);
  }

  private void publicarUsuarioActualizado(Usuario usuario) {
    UsuarioActualizado evento =
        UsuarioActualizado.of(
            usuario.getId().value().toString(),
            usuario.getUsername().value(),
            usuario.getAvatar().url(),
            usuario.getDiscordUserId().value());
    usuarioPublisher.publicarUsuarioActualizado(evento);
  }

  private static void publicarAfterCommit(Runnable action) {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              action.run();
            }
          });
    } else {
      action.run();
    }
  }
}
