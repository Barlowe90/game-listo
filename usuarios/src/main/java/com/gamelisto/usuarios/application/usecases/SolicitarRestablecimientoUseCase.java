package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.SolicitarRestablecimientoCommand;
import com.gamelisto.usuarios.domain.repositories.IEmailService;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Email;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SolicitarRestablecimientoUseCase implements SolicitarRestablecimientoHandle {

  private final RepositorioUsuarios repositorioUsuarios;
  private final IEmailService emailService;

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
