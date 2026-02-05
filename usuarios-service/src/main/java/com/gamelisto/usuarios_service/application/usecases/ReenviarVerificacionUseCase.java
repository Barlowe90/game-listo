package com.gamelisto.usuarios_service.application.usecases;

import com.gamelisto.usuarios_service.application.dto.ReenviarVerificacionCommand;
import com.gamelisto.usuarios_service.application.ports.IEmailService;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioYaVerificadoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Email;
import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReenviarVerificacionUseCase {

  private final RepositorioUsuarios repositorioUsuarios;
  private final IEmailService emailService;

  public ReenviarVerificacionUseCase(
      RepositorioUsuarios repositorioUsuarios, IEmailService emailService) {
    this.repositorioUsuarios = repositorioUsuarios;
    this.emailService = emailService;
  }

  @Transactional
  public void execute(ReenviarVerificacionCommand command) {

    Email email = Email.of(command.email());

    Usuario usuario =
        repositorioUsuarios
            .findByEmail(email)
            .orElseThrow(() -> new UsuarioNoEncontradoException(command.email()));

    if (usuario.getStatus() != EstadoUsuario.PENDIENTE_DE_VERIFICACION) {
      throw new UsuarioYaVerificadoException(command.email());
    }

    usuario.generarTokenVerificacion();
    repositorioUsuarios.save(usuario);

    emailService.sendVerificationEmail(
        usuario.getEmail().value(),
        usuario.getUsername().value(),
        usuario.getTokenVerificacion().value());
  }
}
