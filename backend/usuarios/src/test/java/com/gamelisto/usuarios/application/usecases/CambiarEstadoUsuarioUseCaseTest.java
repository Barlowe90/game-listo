package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.CambiarEstadoUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.application.usecases.admin.CambiarEstadoUsuarioUseCase;
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
@DisplayName("CambiarEstadoUsuarioUseCase - Tests")
class CambiarEstadoUsuarioUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;

  @InjectMocks private CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;

  // ========== CASOS DE Ã‰XITO ==========

  @Test
  @DisplayName("Debe suspender usuario exitosamente")
  void debeSuspenderUsuarioExitosamente() {
    // Arrange
    String usuarioId = UUID.randomUUID().toString();
    Usuario usuario = crearUsuarioActivo(usuarioId);

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CambiarEstadoUsuarioCommand command =
        new CambiarEstadoUsuarioCommand(usuarioId, EstadoUsuario.SUSPENDIDO);

    // Act
    UsuarioResult resultado = cambiarEstadoUsuarioUseCase.execute(command);

    // Assert
    assertNotNull(resultado);
    assertEquals("SUSPENDIDO", resultado.status());
    verify(repositorioUsuarios).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Debe activar usuario suspendido exitosamente")
  void debeActivarUsuarioSuspendidoExitosamente() {
    // Arrange
    String usuarioId = UUID.randomUUID().toString();
    Usuario usuario = crearUsuarioSuspendido(usuarioId);

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CambiarEstadoUsuarioCommand command =
        new CambiarEstadoUsuarioCommand(usuarioId, EstadoUsuario.ACTIVO);

    // Act
    UsuarioResult resultado = cambiarEstadoUsuarioUseCase.execute(command);

    // Assert
    assertNotNull(resultado);
    assertEquals("ACTIVO", resultado.status());
    verify(repositorioUsuarios).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Debe retornar DTO con todos los datos del usuario")
  void debeRetornarDTOConTodosLosDatos() {
    // Arrange
    String usuarioId = UUID.randomUUID().toString();
    Usuario usuario = crearUsuarioActivo(usuarioId);

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CambiarEstadoUsuarioCommand command =
        new CambiarEstadoUsuarioCommand(usuarioId, EstadoUsuario.SUSPENDIDO);

    // Act
    UsuarioResult resultado = cambiarEstadoUsuarioUseCase.execute(command);

    // Assert
    assertNotNull(resultado);
    assertEquals("testuser", resultado.username());
    assertEquals("test@test.com", resultado.email());
    assertEquals("SUSPENDIDO", resultado.status());
  }

  // ========== CASOS DE ERROR ==========

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    // Arrange
    String usuarioIdInexistente = UUID.randomUUID().toString();

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.empty());

    CambiarEstadoUsuarioCommand command =
        new CambiarEstadoUsuarioCommand(usuarioIdInexistente, EstadoUsuario.SUSPENDIDO);

    // Act & Assert
    assertThrows(ApplicationException.class, () -> cambiarEstadoUsuarioUseCase.execute(command));

    verify(repositorioUsuarios, never()).save(any());
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si se intenta activar usuario eliminado")
  void debeLanzarExcepcionSiSeIntentaActivarUsuarioEliminado() {
    // Arrange
    String usuarioId = UUID.randomUUID().toString();
    Usuario usuario = crearUsuarioEliminado(usuarioId);

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));

    CambiarEstadoUsuarioCommand command =
        new CambiarEstadoUsuarioCommand(usuarioId, EstadoUsuario.ACTIVO);

    // Act & Assert
    assertThrows(DomainException.class, () -> cambiarEstadoUsuarioUseCase.execute(command));

    verify(repositorioUsuarios, never()).save(any());
  }

  // ========== MÃ‰TODOS AUXILIARES ==========

  private Usuario crearUsuarioActivo(String id) {
    return Usuario.reconstitute(
        UsuarioId.fromString(id),
        Username.of("testuser"),
        Email.of("test@test.com"),
        PasswordHash.of("$2a$10$hash"),
        Avatar.empty(),
        Rol.USER,
        EstadoUsuario.ACTIVO,
        DiscordUserId.empty(),
        TokenVerificacion.empty(),
        null,
        TokenVerificacion.empty(),
        null);
  }

  private Usuario crearUsuarioSuspendido(String id) {
    return Usuario.reconstitute(
        UsuarioId.fromString(id),
        Username.of("testuser"),
        Email.of("test@test.com"),
        PasswordHash.of("$2a$10$hash"),
        Avatar.empty(),
        Rol.USER,
        EstadoUsuario.SUSPENDIDO,
        DiscordUserId.empty(),
        TokenVerificacion.empty(),
        null,
        TokenVerificacion.empty(),
        null);
  }

  private Usuario crearUsuarioEliminado(String id) {
    return Usuario.reconstitute(
        UsuarioId.fromString(id),
        Username.of("testuser"),
        Email.of("test@test.com"),
        PasswordHash.of("$2a$10$hash"),
        Avatar.empty(),
        Rol.USER,
        EstadoUsuario.ELIMINADO,
        DiscordUserId.empty(),
        TokenVerificacion.empty(),
        null,
        TokenVerificacion.empty(),
        null);
  }
}



