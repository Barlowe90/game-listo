package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.EditarPerfilUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
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
class EditarPerfilUsuarioUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;

  @Mock private IUsuarioPublisher usuarioPublisher;

  @InjectMocks private EditarPerfilUsuarioUseCase editarPerfilUsuarioUseCase;

  @Test
  @DisplayName("Debe editar múltiples campos a la vez")
  void debeEditarMultiplesCamposALaVez() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    EditarPerfilUsuarioCommand command =
        new EditarPerfilUsuarioCommand(usuarioIdString, "https://example.com/avatar.jpg", "ENG");

    Usuario usuario = crearUsuarioDefault(UsuarioId.fromString(usuarioIdString));

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UsuarioDTO resultado = editarPerfilUsuarioUseCase.execute(command);

    // Assert
    assertEquals("https://example.com/avatar.jpg", resultado.avatar());
    assertEquals("ENG", resultado.language());
  }

  @Test
  @DisplayName("Debe ignorar campos nulos sin modificar el usuario")
  void debeIgnorarCamposNulosSinModificarUsuario() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    EditarPerfilUsuarioCommand command =
        new EditarPerfilUsuarioCommand(usuarioIdString, null, null);

    Usuario usuario = crearUsuarioDefault(UsuarioId.fromString(usuarioIdString));
    String avatarOriginal = usuario.getAvatar().url();
    Idioma idiomaOriginal = usuario.getLanguage();

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UsuarioDTO resultado = editarPerfilUsuarioUseCase.execute(command);

    // Assert
    assertEquals(avatarOriginal, resultado.avatar());
    assertEquals(idiomaOriginal.name(), resultado.language());
  }

  @Test
  @DisplayName("Debe lanzar excepción si usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    EditarPerfilUsuarioCommand command =
        new EditarPerfilUsuarioCommand(usuarioIdString, "https://example.com/avatar.jpg", null);

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.empty());

    // Act & Assert
    ApplicationException exception =
        assertThrows(ApplicationException.class, () -> editarPerfilUsuarioUseCase.execute(command));

    assertNotNull(exception);
    verify(repositorioUsuarios).findById(any(UsuarioId.class));
    verify(repositorioUsuarios, never()).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción si ID tiene formato inválido")
  void debeLanzarExcepcionSiIdTieneFormatoInvalido() {
    // Arrange
    EditarPerfilUsuarioCommand command =
        new EditarPerfilUsuarioCommand("id-invalido", "https://example.com/avatar.jpg", null);

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> editarPerfilUsuarioUseCase.execute(command));

    assertTrue(exception.getMessage().contains("Formato de UUID inválido"));
    verify(repositorioUsuarios, never()).findById(any(UsuarioId.class));
  }

  @Test
  @DisplayName("Debe validar URL del avatar")
  void debeValidarUrlDelAvatar() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    String urlLarga = "https://example.com/" + "a".repeat(500);
    EditarPerfilUsuarioCommand command =
        new EditarPerfilUsuarioCommand(usuarioIdString, urlLarga, null);

    Usuario usuario = crearUsuarioDefault(UsuarioId.fromString(usuarioIdString));

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> editarPerfilUsuarioUseCase.execute(command));

    assertTrue(exception.getMessage().contains("no puede exceder 500 caracteres"));
  }

  @Test
  @DisplayName("Debe lanzar excepción si idioma es inválido")
  void debeLanzarExcepcionSiIdiomaEsInvalido() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    EditarPerfilUsuarioCommand command =
        new EditarPerfilUsuarioCommand(usuarioIdString, null, "IDIOMA_INVALIDO");

    Usuario usuario = crearUsuarioDefault(UsuarioId.fromString(usuarioIdString));

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> editarPerfilUsuarioUseCase.execute(command));
  }

  // Helper method
  private Usuario crearUsuarioDefault(UsuarioId id) {
    return Usuario.reconstitute(
        id,
        Username.of("testuser"),
        Email.of("test@test.com"),
        PasswordHash.of("$2a$10$hash"),
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
