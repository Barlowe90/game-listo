package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.RestablecerContrasenaCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.*;
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

  @Test
  @DisplayName("Debe restablecer contraseña exitosamente con token válido")
  void debeRestablecerContrasenaExitosamente() {
    // Arrange
    TokenVerificacion token = TokenVerificacion.generate();
    Usuario usuario = crearUsuarioConToken(token, Instant.now().plusSeconds(3600));
    String hashNuevo = "$2a$10$hashedNewPassword";

    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuario));
    when(passwordEncoder.encode("newPassword123")).thenReturn(hashNuevo);
    when(repositorioUsuarios.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    restablecerContrasenaUseCase.execute(
        new RestablecerContrasenaCommand(token.value(), "newPassword123", "test@test.com"));

    // Assert
    verify(repositorioUsuarios).save(any(Usuario.class));
    assertEquals(hashNuevo, usuario.getPasswordHash().value());
    assertTrue(usuario.getTokenRestablecimiento().isEmpty());
  }

  @Test
  @DisplayName("Debe lanzar excepción si el usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    // Arrange
    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ApplicationException.class,
        () ->
            restablecerContrasenaUseCase.execute(
                new RestablecerContrasenaCommand("token", "password", "noexiste@test.com")));

    verify(repositorioUsuarios, never()).save(any());
  }

  @Test
  @DisplayName("Debe lanzar excepción si el token es inválido o ha expirado")
  void debeLanzarExcepcionSiTokenEsInvalidoOExpirado() {
    // Arrange
    TokenVerificacion token = TokenVerificacion.generate();
    Usuario usuario = crearUsuarioConToken(token, Instant.now().minusSeconds(3600));

    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuario));

    // Act & Assert
    assertThrows(
        ApplicationException.class,
        () ->
            restablecerContrasenaUseCase.execute(
                new RestablecerContrasenaCommand(token.value(), "password", "test@test.com")));

    verify(repositorioUsuarios, never()).save(any());
  }

  // ========== FUNCION AUXILIAR ==========

  private Usuario crearUsuarioConToken(TokenVerificacion token, Instant expiracion) {
    return Usuario.reconstitute(
        UsuarioId.generate(),
        Username.of("testuser"),
        Email.of("test@test.com"),
        PasswordHash.of("$2a$10$oldHash"),
        Avatar.empty(),
        Rol.USER,
        Idioma.ESP,
        EstadoUsuario.ACTIVO,
        DiscordUserId.empty(),
        DiscordUsername.empty(),
        TokenVerificacion.empty(),
        null,
        token,
        expiracion);
  }
}
