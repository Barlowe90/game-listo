package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.VerificarEmailCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.events.UsuarioCreado;
import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.TokenVerificacion;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerificarEmailUseCase implements VerificarEmailHandle {

  private final RepositorioUsuarios repositorioUsuarios;
  private final IUsuarioPublisher eventosPublisher;

  @Transactional
  public void execute(VerificarEmailCommand command) {
    TokenVerificacion token = TokenVerificacion.of(command.token());

    Usuario usuario = recuperarUsuario(token);

    comprobarHaVerificadoEmailUsuario(usuario, token);

    repositorioUsuarios.save(usuario);

    publicarAfterCommit(() -> publicarEventoUsuarioCreado(usuario));
  }

  private @NonNull Usuario recuperarUsuario(TokenVerificacion token) {
    return repositorioUsuarios
        .findByTokenVerificacion(token)
        .orElseThrow(() -> new ApplicationException("Token inválido"));
  }

  private void publicarEventoUsuarioCreado(Usuario usuario) {
    UsuarioCreado evento =
        UsuarioCreado.of(
            usuario.getId().value().toString(),
            usuario.getUsername().value(),
            usuario.getEmail().value(),
            usuario.getAvatar().toString(),
            usuario.getRole() != null ? usuario.getRole().name() : null,
            usuario.getLanguage() != null ? usuario.getLanguage().name() : null,
            usuario.getStatus() != null ? usuario.getStatus().name() : null,
            usuario.getDiscordUserId() != null ? usuario.getDiscordUserId().value() : null,
            usuario.getDiscordUsername() != null ? usuario.getDiscordUsername().value() : null);
    eventosPublisher.publicarUsuarioCreado(evento);
  }

  private static void comprobarHaVerificadoEmailUsuario(Usuario usuario, TokenVerificacion token) {
    try {
      usuario.verificarEmail(token);
    } catch (IllegalStateException e) {
      throw new ApplicationException(
          "El usuario con email " + usuario.getEmail().value() + " ya ha sido verificado");
    } catch (IllegalArgumentException e) {
      throw new ApplicationException(e.getMessage());
    }
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
