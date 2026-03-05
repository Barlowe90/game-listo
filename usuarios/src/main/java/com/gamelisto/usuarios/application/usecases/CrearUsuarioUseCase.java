package com.gamelisto.usuarios.application.usecases;

import com.gamelisto.usuarios.application.dto.CrearUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.IEmailService;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.Email;
import com.gamelisto.usuarios.domain.usuario.PasswordHash;
import com.gamelisto.usuarios.domain.usuario.Username;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearUsuarioUseCase {

  private final RepositorioUsuarios repositorioUsuarios;
  private final PasswordEncoder passwordEncoder;
  private final IEmailService emailService;

  @Transactional
  public UsuarioDTO execute(CrearUsuarioCommand command) {

    Username username = Username.of(command.username());
    Email email = Email.of(command.email());

    comprobarSiExisteUsuarioParaLanzarExcepcion(command, username);
    comprobarSiExisteUsuarioConEmailParaLanzarExcepcion(command, email);

    PasswordHash passwordHash = hashearPassword(command);
    Usuario usuarioGuardado = crearUsuario(username, email, passwordHash);

    enviarUsuarioEmailVerificacion(usuarioGuardado);

    return UsuarioDTO.from(usuarioGuardado);
  }

  private @NonNull PasswordHash hashearPassword(CrearUsuarioCommand command) {
    String hashedPassword = passwordEncoder.encode(command.password());
    return PasswordHash.of(hashedPassword);
  }

  private Usuario crearUsuario(Username username, Email email, PasswordHash passwordHash) {
    Usuario usuario = Usuario.create(username, email, passwordHash);
    return repositorioUsuarios.save(usuario);
  }

  private void comprobarSiExisteUsuarioConEmailParaLanzarExcepcion(
      CrearUsuarioCommand command, Email email) {
    if (repositorioUsuarios.existsByEmail(email)) {
      throw new ApplicationException("El email '" + command.email() + "' ya está registrado");
    }
  }

  private void enviarUsuarioEmailVerificacion(Usuario usuarioGuardado) {
    String verificationToken = usuarioGuardado.getTokenVerificacion().value();

    emailService.sendVerificationEmail(
        usuarioGuardado.getEmail().value(),
        usuarioGuardado.getUsername().value(),
        verificationToken);
  }

  private void comprobarSiExisteUsuarioParaLanzarExcepcion(
      CrearUsuarioCommand command, Username username) {
    if (repositorioUsuarios.existsByUsername(username)) {
      throw new ApplicationException("El username '" + command.username() + "' ya está en uso");
    }
  }
}
