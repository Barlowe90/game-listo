package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.CambiarContrasenaCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.application.usecases.usuarios.CambiarContrasenaUseCase;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("CambiarContrasenaUseCase - Tests")
class CambiarContrasenaUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private CambiarContrasenaUseCase cambiarContrasenaUseCase;

  // ========== CASOS DE ÉXITO ==========

  @Test
  @DisplayName("Debe cambiar contraseña exitosamente cuando la actual es correcta")
  void debeCambiarContrasenaExitosamente() {
    // Arrange
    UUID usuarioId = UUID.randomUUID();
    String contrasenaActual = "password123";
    String contrasenaNueva = "newPassword456";
    String hashActual = "$2a$10$hashActual";
    String hashNuevo = "$2a$10$hashNuevo";

    Usuario usuario = crearUsuarioConPassword(usuarioId, hashActual);

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(passwordEncoder.matches(contrasenaActual, hashActual)).thenReturn(true);
    when(passwordEncoder.encode(contrasenaNueva)).thenReturn(hashNuevo);
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CambiarContrasenaCommand command =
        new CambiarContrasenaCommand(usuarioId, contrasenaActual, contrasenaNueva);

    // Act
    cambiarContrasenaUseCase.execute(command);

    // Assert
    verify(repositorioUsuarios).findById(any(UsuarioId.class));
    verify(passwordEncoder).matches(contrasenaActual, hashActual);
    verify(passwordEncoder).encode(contrasenaNueva);
    verify(repositorioUsuarios).save(argThat(u -> u.getPasswordHash().value().equals(hashNuevo)));
  }

  // ========== CASOS DE ERROR ==========

  @Test
  @DisplayName("Debe lanzar excepción si el usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    // Arrange
    UUID usuarioIdInexistente = UUID.randomUUID();

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.empty());

    CambiarContrasenaCommand command =
        new CambiarContrasenaCommand(usuarioIdInexistente, "password", "newPassword");

    // Act & Assert
    assertThrows(ApplicationException.class, () -> cambiarContrasenaUseCase.execute(command));

    verify(repositorioUsuarios, never()).save(any());
    verify(passwordEncoder, never()).encode(anyString());
  }

  @Test
  @DisplayName("Debe lanzar excepción si la contraseña actual es incorrecta")
  void debeLanzarExcepcionSiContrasenaActualEsIncorrecta() {
    // Arrange
    UUID usuarioId = UUID.randomUUID();
    String hashActual = "$2a$10$hashActual";

    Usuario usuario = crearUsuarioConPassword(usuarioId, hashActual);

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(passwordEncoder.matches("contrasenaIncorrecta", hashActual)).thenReturn(false);

    CambiarContrasenaCommand command =
        new CambiarContrasenaCommand(usuarioId, "contrasenaIncorrecta", "newPassword");

    // Act & Assert
    ApplicationException exception =
        assertThrows(ApplicationException.class, () -> cambiarContrasenaUseCase.execute(command));

    assertTrue(exception.getMessage().contains("contraseña actual no es correcta"));
    verify(repositorioUsuarios, never()).save(any());
    verify(passwordEncoder, never()).encode(anyString());
  }

  // ========== MÉTODOS AUXILIARES ==========

  private Usuario crearUsuarioConPassword(UUID id, String passwordHash) {
    return Usuario.reconstitute(
        UsuarioId.of(id),
        Username.of("testuser"),
        Email.of("test@test.com"),
        PasswordHash.of(passwordHash),
        Avatar.empty(),
        Rol.USER,
        Idioma.ESP,
        EstadoUsuario.ACTIVO,
        DiscordUserId.empty(),
        DiscordUsername.empty(),
        TokenVerificacion.empty(),
        null,
        TokenVerificacion.empty(),
        null);
  }
}
