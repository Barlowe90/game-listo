package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.application.usecases.usuarios.ObtenerUsuarioPorIdUseCase;
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
class ObtenerUsuarioPorIdUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;

  @InjectMocks private ObtenerUsuarioPorIdUseCase obtenerUsuarioPorIdUseCase;

  @Test
  @DisplayName("Debe obtener usuario por ID exitosamente")
  void debeObtenerUsuarioPorIdExitosamente() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);

    Usuario usuario =
        Usuario.reconstitute(
            usuarioId,
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

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));

    // Act
    UsuarioResult resultado = obtenerUsuarioPorIdUseCase.execute(usuarioIdString);

    // Assert
    assertNotNull(resultado);
    assertEquals(usuarioIdString, resultado.id());
    assertEquals("testuser", resultado.username());
    assertEquals("test@test.com", resultado.email());
    assertEquals("ACTIVO", resultado.status());

    verify(repositorioUsuarios).findById(any(UsuarioId.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción si usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.empty());

    // Act & Assert
    ApplicationException exception =
        assertThrows(
            ApplicationException.class, () -> obtenerUsuarioPorIdUseCase.execute(usuarioIdString));

    assertNotNull(exception);
    verify(repositorioUsuarios).findById(any(UsuarioId.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción si ID tiene formato inválido")
  void debeLanzarExcepcionSiIdTieneFormatoInvalido() {
    // Arrange
    String idInvalido = "no-es-uuid";

    // Act & Assert
    DomainException exception =
        assertThrows(DomainException.class, () -> obtenerUsuarioPorIdUseCase.execute(idInvalido));

    assertTrue(exception.getMessage().contains("Formato de UUID inválido"));
    verify(repositorioUsuarios, never()).findById(any(UsuarioId.class));
  }

  @Test
  @DisplayName("Debe retornar DTO con todos los campos del usuario")
  void debeRetornarDTOConTodosLosCampos() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);

    Usuario usuario =
        Usuario.reconstitute(
            usuarioId,
            Username.of("jugador"),
            Email.of("jugador@test.com"),
            PasswordHash.of("$2a$10$hash"),
            Avatar.of("https://example.com/avatar.jpg"),
            Rol.ADMIN,
            EstadoUsuario.SUSPENDIDO,
            DiscordUserId.of("123456"),
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null);

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));

    // Act
    UsuarioResult resultado = obtenerUsuarioPorIdUseCase.execute(usuarioIdString);

    // Assert
    assertEquals(usuarioIdString, resultado.id());
    assertEquals("jugador", resultado.username());
    assertEquals("jugador@test.com", resultado.email());
    assertEquals("https://example.com/avatar.jpg", resultado.avatar());
    assertEquals("ADMIN", resultado.role());
    assertEquals("SUSPENDIDO", resultado.status());
    assertEquals("123456", resultado.discordUserId());
  }

  @Test
  @DisplayName("Debe convertir correctamente ID de string a UsuarioId")
  void debeConvertirCorrectamenteIdDeStringAUsuarioId() {
    // Arrange
    UUID uuid = UUID.randomUUID();
    String usuarioIdString = uuid.toString();
    UsuarioId expectedId = UsuarioId.fromString(usuarioIdString);

    Usuario usuario = crearUsuarioDefault(expectedId);

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));

    // Act
    obtenerUsuarioPorIdUseCase.execute(usuarioIdString);

    // Assert
    verify(repositorioUsuarios).findById(argThat(id -> id.value().equals(uuid)));
  }

  @Test
  @DisplayName("Debe manejar UUID con mayúsculas y minúsculas")
  void debeManejareUUIDConMayusculasYMinusculas() {
    // Arrange
    String usuarioIdUpper = "550E8400-E29B-41D4-A716-446655440000";
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdUpper);
    Usuario usuario = crearUsuarioDefault(usuarioId);

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));

    // Act
    UsuarioResult resultado = obtenerUsuarioPorIdUseCase.execute(usuarioIdUpper);

    // Assert
    assertNotNull(resultado);
    assertEquals(usuarioIdUpper.toLowerCase(), resultado.id());
  }

  // Helper method
  private Usuario crearUsuarioDefault(UsuarioId id) {
    return Usuario.reconstitute(
        id,
        Username.of("test"),
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
}
