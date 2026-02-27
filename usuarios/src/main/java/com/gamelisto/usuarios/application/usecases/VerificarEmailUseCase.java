package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.VerificarEmailCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.events.EmailVerificado;
import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.TokenVerificacion;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerificarEmailUseCase {

  private final RepositorioUsuarios repositorioUsuarios;
  private final IUsuarioPublisher eventosPublisher;
  private static final String ROUTING_KEY_SUFFIX = "usuario.verificado";

  public VerificarEmailUseCase(
      RepositorioUsuarios repositorioUsuarios, IUsuarioPublisher eventosPublisher) {
    this.repositorioUsuarios = repositorioUsuarios;
    this.eventosPublisher = eventosPublisher;
  }

  @Transactional
  public void execute(VerificarEmailCommand command) {
    TokenVerificacion token = TokenVerificacion.of(command.token());

    Usuario usuario =
        repositorioUsuarios
            .findByTokenVerificacion(token)
            .orElseThrow(() -> new ApplicationException("Token inválido"));

    try {
      usuario.verificarEmail(token);
    } catch (IllegalStateException e) {
      throw new ApplicationException(
          "El usuario con email " + usuario.getEmail().value() + " ya ha sido verificado");
    } catch (IllegalArgumentException e) {
      throw new ApplicationException(e.getMessage());
    }

    EmailVerificado evento =
        EmailVerificado.of(usuario.getId().value().toString(), usuario.getEmail().value());
    eventosPublisher.publish(ROUTING_KEY_SUFFIX, evento);

    repositorioUsuarios.save(usuario);
  }
}
