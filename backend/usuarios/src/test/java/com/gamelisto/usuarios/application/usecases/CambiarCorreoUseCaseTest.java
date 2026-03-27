package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.CambiarCorreoCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.application.usecases.usuarios.CambiarCorreoUseCase;
import com.gamelisto.usuarios.domain.exceptions.DomainException;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("CambiarCorreoUseCase - Tests")
class CambiarCorreoUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;

  @InjectMocks private CambiarCorreoUseCase cambiarCorreoUseCase;

  // ========== CASOS DE ÉXITO ==========

  @Test
  @DisplayName("Debe cambiar correo exitosamente cuando el nuevo email no está registrado")
  void debeCambiarCorreoExitosamente() {
    // Arrange
    UUID usuarioId = UUID.randomUUID();
    String emailActual = "usuario@ejemplo.com";
    String emailNuevo = "nuevo@ejemplo.com";

    Usuario usuario = crearUsuarioActivo(usuarioId.toString(), emailActual);

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.empty());
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CambiarCorreoCommand command = new CambiarCorreoCommand(usuarioId, emailNuevo);

    // Act
    cambiarCorreoUseCase.execute(command);

    // Assert
    verify(repositorioUsuarios).findById(any(UsuarioId.class));
    verify(repositorioUsuarios).findByEmail(any(Email.class));
    verify(repositorioUsuarios)
        .save(
            argThat(
                u ->
                    u.getEmail().value().equals(emailNuevo)
                        && u.getStatus() == EstadoUsuario.PENDIENTE_DE_VERIFICACION
                        && u.getTokenVerificacion() != null
                        && !u.getTokenVerificacion().isEmpty()));
  }

  // ========== CASOS DE ERROR ==========

  @Test
  @DisplayName("Debe lanzar excepción si el usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    // Arrange
    UUID usuarioIdInexistente = UUID.randomUUID();

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.empty());

    CambiarCorreoCommand command =
        new CambiarCorreoCommand(usuarioIdInexistente, "nuevo@ejemplo.com");

    // Act & Assert
    ApplicationException exception =
        assertThrows(ApplicationException.class, () -> cambiarCorreoUseCase.execute(command));

    assertTrue(exception.getMessage().contains(usuarioIdInexistente.toString()));
    verify(repositorioUsuarios, never()).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción si el email ya está registrado por otro usuario")
  void debeLanzarExcepcionSiEmailYaRegistrado() {
    // Arrange
    UUID usuarioId = UUID.randomUUID();
    UUID otroUsuarioId = UUID.randomUUID();
    String emailDuplicado = "duplicado@ejemplo.com";

    Usuario usuario = crearUsuarioActivo(usuarioId.toString(), "original@ejemplo.com");
    Usuario otroUsuario = crearUsuarioActivo(otroUsuarioId.toString(), emailDuplicado);

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(otroUsuario));

    CambiarCorreoCommand command = new CambiarCorreoCommand(usuarioId, emailDuplicado);

    // Act & Assert
    ApplicationException exception =
        assertThrows(ApplicationException.class, () -> cambiarCorreoUseCase.execute(command));

    assertTrue(exception.getMessage().contains(emailDuplicado));
    verify(repositorioUsuarios, never()).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción con formato de email inválido")
  void debeLanzarExcepcionConEmailInvalido() {
    // Arrange
    UUID usuarioId = UUID.randomUUID();

    CambiarCorreoCommand command = new CambiarCorreoCommand(usuarioId, "email-invalido");

    // Act & Assert - La excepción se lanza al crear el Email Value Object
    assertThrows(DomainException.class, () -> cambiarCorreoUseCase.execute(command));

    verify(repositorioUsuarios, never()).findById(any(UsuarioId.class));
    verify(repositorioUsuarios, never()).save(any(Usuario.class));
  }

  // ========== HELPERS ==========

  private Usuario crearUsuarioActivo(String id, String email) {
    return Usuario.reconstitute(
        UsuarioId.fromString(id),
        Username.of("testuser"),
        Email.of(email),
        PasswordHash.of("$2a$10$hashedPassword"),
        Avatar.empty(),
        Rol.USER,
        EstadoUsuario.ACTIVO,
        DiscordUserId.empty(),
        TokenVerificacion.empty(),
        null,
        TokenVerificacion.empty(),
        null);
  }
}
