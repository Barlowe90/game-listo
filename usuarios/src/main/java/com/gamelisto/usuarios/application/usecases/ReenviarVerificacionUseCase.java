package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.ReenviarVerificacionCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.IEmailService;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Email;
import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReenviarVerificacionUseCase implements ReenviarVerificacionHandle {

  private final RepositorioUsuarios repositorioUsuarios;
  private final IEmailService emailService;

  @Transactional
  public void execute(ReenviarVerificacionCommand command) {

    Email email = Email.of(command.email());

    Usuario usuario =
        repositorioUsuarios
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new ApplicationException(
                        "Usuario no encontrado con email: " + command.email()));

    if (usuario.getStatus() != EstadoUsuario.PENDIENTE_DE_VERIFICACION) {
      throw new ApplicationException(
          "El usuario con email " + command.email() + " ya ha sido verificado");
    }

    usuario.generarTokenVerificacion();
    repositorioUsuarios.save(usuario);

    emailService.sendVerificationEmail(
        usuario.getEmail().value(),
        usuario.getUsername().value(),
        usuario.getTokenVerificacion().value());
  }
}
