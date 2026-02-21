package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.SolicitarRestablecimientoCommand;
import com.gamelisto.usuarios.domain.repositories.IEmailService;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Email;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitarRestablecimientoUseCase {

  private final RepositorioUsuarios repositorioUsuarios;
  private final IEmailService emailService;

  public SolicitarRestablecimientoUseCase(
      RepositorioUsuarios repositorioUsuarios, IEmailService emailService) {
    this.repositorioUsuarios = repositorioUsuarios;
    this.emailService = emailService;
  }

  @Transactional
  public void execute(SolicitarRestablecimientoCommand command) {

    Email email = Email.of(command.email());
    Optional<Usuario> usuarioOpt = repositorioUsuarios.findByEmail(email);

    if (usuarioOpt.isEmpty()) {
      return; // No revelar que el email no existe por seguridad
    }

    Usuario usuario = usuarioOpt.get();
    usuario.generarTokenRestablecimiento();
    repositorioUsuarios.save(usuario);

    emailService.sendPasswordResetEmail(
        usuario.getEmail().value(),
        usuario.getUsername().value(),
        usuario.getTokenRestablecimiento().value());
  }
}
