package com.gamelisto.usuarios_service.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios_service.application.dto.RestablecerContrasenaCommand;
import com.gamelisto.usuarios_service.domain.exceptions.TokenVerificacionInvalidoException;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.*;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("RestablecerContrasenaUseCase - Tests")
class RestablecerContrasenaUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private RestablecerContrasenaUseCase restablecerContrasenaUseCase;

  // ========== CASOS DE ÉXITO ==========

  @Test
  @DisplayName("Debe restablecer contraseña exitosamente con token válido")
  void debeRestablecerContrasenaExitosamente() {
    // Arrange
    String email = "test@test.com";
    TokenVerificacion token = TokenVerificacion.generate();
    String nuevaContrasena = "newPassword123";
    String hashNuevo = "$2a$10$hashedNewPassword";

    Usuario usuario = crearUsuarioConTokenRestablecimiento(email, token);

    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuario));
    when(passwordEncoder.encode(nuevaContrasena)).thenReturn(hashNuevo);
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    RestablecerContrasenaCommand command =
        new RestablecerContrasenaCommand(token.value(), nuevaContrasena, email);

    // Act
    restablecerContrasenaUseCase.execute(command);

    // Assert
    verify(repositorioUsuarios).findByEmail(any(Email.class));
    verify(passwordEncoder).encode(nuevaContrasena);
    verify(repositorioUsuarios).save(any(Usuario.class));

    assertEquals(hashNuevo, usuario.getPasswordHash().value());
  }

  @Test
  @DisplayName("Debe invalidar token después de restablecer contraseña")
  void debeInvalidarTokenDespuesDeRestablecer() {
    // Arrange
    String email = "test@test.com";
    TokenVerificacion token = TokenVerificacion.generate();

    Usuario usuario = crearUsuarioConTokenRestablecimiento(email, token);

    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuario));
    when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    RestablecerContrasenaCommand command =
        new RestablecerContrasenaCommand(token.value(), "newPassword", email);

    // Act
    restablecerContrasenaUseCase.execute(command);

    // Assert
    assertTrue(usuario.getTokenVerificacion().isEmpty());
    assertNull(usuario.getTokenVerificacionExpiracion());
  }

  @Test
  @DisplayName("Debe hashear la nueva contraseña antes de guardar")
  void debeHashearNuevaContrasena() {
    // Arrange
    String email = "test@test.com";
    TokenVerificacion token = TokenVerificacion.generate();
    String nuevaContrasena = "plainPassword";
    String hashNuevo = "$2a$10$encodedPassword";

    Usuario usuario = crearUsuarioConTokenRestablecimiento(email, token);

    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuario));
    when(passwordEncoder.encode(nuevaContrasena)).thenReturn(hashNuevo);
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    RestablecerContrasenaCommand command =
        new RestablecerContrasenaCommand(token.value(), nuevaContrasena, email);

    // Act
    restablecerContrasenaUseCase.execute(command);

    // Assert
    verify(passwordEncoder).encode(nuevaContrasena);
    assertEquals(hashNuevo, usuario.getPasswordHash().value());
  }

  // ========== CASOS DE ERROR ==========

  @Test
  @DisplayName("Debe lanzar excepción si el usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    // Arrange
    String emailInexistente = "noexiste@test.com";

    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.empty());

    RestablecerContrasenaCommand command =
        new RestablecerContrasenaCommand("token", "password", emailInexistente);

    // Act & Assert
    assertThrows(
        UsuarioNoEncontradoException.class, () -> restablecerContrasenaUseCase.execute(command));

    verify(repositorioUsuarios, never()).save(any());
    verify(passwordEncoder, never()).encode(anyString());
  }

  @Test
  @DisplayName("Debe lanzar excepción si el token es inválido")
  void debeLanzarExcepcionSiTokenEsInvalido() {
    // Arrange
    String email = "test@test.com";
    TokenVerificacion tokenCorrecto = TokenVerificacion.generate();
    Usuario usuario = crearUsuarioConTokenRestablecimiento(email, tokenCorrecto);

    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuario));

    RestablecerContrasenaCommand command =
        new RestablecerContrasenaCommand("token_incorrecto", "password", email);

    // Act & Assert
    assertThrows(
        TokenVerificacionInvalidoException.class,
        () -> restablecerContrasenaUseCase.execute(command));

    verify(repositorioUsuarios, never()).save(any());
    verify(passwordEncoder, never()).encode(anyString());
  }

  @Test
  @DisplayName("Debe lanzar excepción si el token ha expirado")
  void debeLanzarExcepcionSiTokenHaExpirado() {
    // Arrange
    String email = "test@test.com";
    TokenVerificacion token = TokenVerificacion.generate();
    Usuario usuario = crearUsuarioConTokenExpirado(email, token);

    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuario));

    RestablecerContrasenaCommand command =
        new RestablecerContrasenaCommand(token.value(), "password", email);

    // Act & Assert
    assertThrows(
        TokenVerificacionInvalidoException.class,
        () -> restablecerContrasenaUseCase.execute(command));

    verify(repositorioUsuarios, never()).save(any());
    verify(passwordEncoder, never()).encode(anyString());
  }

  @Test
  @DisplayName("Debe lanzar excepción si el usuario no tiene token de restablecimiento")
  void debeLanzarExcepcionSiUsuarioNoTieneToken() {
    // Arrange
    String email = "test@test.com";
    Usuario usuario = crearUsuarioSinToken(email);

    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuario));

    RestablecerContrasenaCommand command =
        new RestablecerContrasenaCommand("cualquier_token", "password", email);

    // Act & Assert
    assertThrows(
        TokenVerificacionInvalidoException.class,
        () -> restablecerContrasenaUseCase.execute(command));

    verify(repositorioUsuarios, never()).save(any());
  }

  // ========== MÉTODOS AUXILIARES ==========

  private Usuario crearUsuarioConTokenRestablecimiento(String email, TokenVerificacion token) {
    return Usuario.reconstitute(
        UsuarioId.generate(),
        Username.of("testuser"),
        Email.of(email),
        PasswordHash.of("$2a$10$oldHash"),
        Avatar.empty(),
        Instant.now(),
        Instant.now(),
        Rol.USER,
        Idioma.ESP,
        true,
        EstadoUsuario.ACTIVO,
        DiscordUserId.empty(),
        DiscordUsername.empty(),
        null,
        TokenVerificacion.empty(),
        null,
        token,
        Instant.now().plusSeconds(3600) // Token válido por 1 hora
        );
  }

  private Usuario crearUsuarioConTokenExpirado(String email, TokenVerificacion token) {
    return Usuario.reconstitute(
        UsuarioId.generate(),
        Username.of("testuser"),
        Email.of(email),
        PasswordHash.of("$2a$10$hash"),
        Avatar.empty(),
        Instant.now(),
        Instant.now(),
        Rol.USER,
        Idioma.ESP,
        true,
        EstadoUsuario.ACTIVO,
        DiscordUserId.empty(),
        DiscordUsername.empty(),
        null,
        TokenVerificacion.empty(),
        null,
        token,
        Instant.now().minusSeconds(3600) // Token expirado hace 1 hora
        );
  }

  private Usuario crearUsuarioSinToken(String email) {
    return Usuario.reconstitute(
        UsuarioId.generate(),
        Username.of("testuser"),
        Email.of(email),
        PasswordHash.of("$2a$10$hash"),
        Avatar.empty(),
        Instant.now(),
        Instant.now(),
        Rol.USER,
        Idioma.ESP,
        true,
        EstadoUsuario.ACTIVO,
        DiscordUserId.empty(),
        DiscordUsername.empty(),
        null,
        TokenVerificacion.empty(),
        null,
        TokenVerificacion.empty(),
        null);
  }
}
