package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.application.usecases.discord.DesvincularDiscordUseCase;
import com.gamelisto.usuarios.domain.events.UsuarioActualizado;
import com.gamelisto.usuarios.domain.exceptions.DomainException;
import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DesvincularDiscordUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;
  @Mock private IUsuarioPublisher usuarioPublisher;

  @InjectMocks private DesvincularDiscordUseCase desvincularDiscordUseCase;

  private Usuario usuario;

  @BeforeEach
  void setUp() {
    usuario =
        Usuario.create(
            Username.of("testuser"), Email.of("test@test.com"), PasswordHash.of("$2a$10$hash"));

    // Vincular Discord por defecto
    usuario.linkDiscord(DiscordUserId.of("123456789"));
  }

  @Test
  @DisplayName("Debe desvincular cuenta de Discord exitosamente")
  void debeDesvincularDiscordExitosamente() {
    // Arrange
    java.util.UUID usuarioId = usuario.getId().value();

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UsuarioResult resultado = desvincularDiscordUseCase.execute(usuarioId);

    // Assert
    assertNotNull(resultado);
    assertNull(resultado.discordUserId());

    verify(repositorioUsuarios).findById(any(UsuarioId.class));
    verify(repositorioUsuarios).save(any(Usuario.class));
    ArgumentCaptor<UsuarioActualizado> eventCaptor =
        ArgumentCaptor.forClass(UsuarioActualizado.class);
    verify(usuarioPublisher).publicarUsuarioActualizado(eventCaptor.capture());
    assertNull(eventCaptor.getValue().discordUserId());
  }

  @Test
  @DisplayName("Debe lanzar excepción si usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    // Arrange
    java.util.UUID usuarioId = java.util.UUID.fromString("00000000-0000-0000-0000-000000000000");

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(ApplicationException.class, () -> desvincularDiscordUseCase.execute(usuarioId));

    verify(repositorioUsuarios).findById(any(UsuarioId.class));
    verify(repositorioUsuarios, never()).save(any(Usuario.class));
    verify(usuarioPublisher, never()).publicarUsuarioActualizado(any());
  }

  @Test
  @DisplayName("Debe funcionar correctamente aunque Discord no esté vinculado")
  void debeFuncionarAunqueDiscordNoVinculado() {
    // Arrange
    Usuario usuarioSinDiscord =
        Usuario.create(
            Username.of("usersindiscord"),
            Email.of("sindiscord@test.com"),
            PasswordHash.of("$2a$10$hash"));

    java.util.UUID usuarioId = usuarioSinDiscord.getId().value();

    when(repositorioUsuarios.findById(any(UsuarioId.class)))
        .thenReturn(Optional.of(usuarioSinDiscord));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UsuarioResult resultado = desvincularDiscordUseCase.execute(usuarioId);

    // Assert
    assertNotNull(resultado);
    assertNull(resultado.discordUserId());

    verify(repositorioUsuarios).save(any(Usuario.class));
    verify(usuarioPublisher).publicarUsuarioActualizado(any(UsuarioActualizado.class));
  }

  @Test
  @DisplayName("Debe eliminar todos los datos de Discord al desvincular")
  void debeEliminarTodosDatosDiscord() {
    // Arrange
    java.util.UUID usuarioId = usuario.getId().value();

    // Verificar que Discord está vinculado antes
    assertFalse(usuario.getDiscordUserId().isEmpty());

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(
            invocation -> {
              Usuario savedUsuario = invocation.getArgument(0);
              // Verificar el estado del usuario después de unlinkDiscord
              assertTrue(savedUsuario.getDiscordUserId().isEmpty());
              return savedUsuario;
            });

    // Act
    UsuarioResult resultado = desvincularDiscordUseCase.execute(usuarioId);

    // Assert
    assertNull(resultado.discordUserId());

    verify(repositorioUsuarios).save(any(Usuario.class));
    verify(usuarioPublisher).publicarUsuarioActualizado(any(UsuarioActualizado.class));
  }

  @Test
  @DisplayName("Debe preservar otros datos del usuario al desvincular Discord")
  void debePreservarOtrosDatosAlDesvincular() {
    // Arrange
    java.util.UUID usuarioId = usuario.getId().value();
    String usernameOriginal = usuario.getUsername().value();
    String emailOriginal = usuario.getEmail().value();

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UsuarioResult resultado = desvincularDiscordUseCase.execute(usuarioId);

    // Assert
    assertEquals(usernameOriginal, resultado.username());
    assertEquals(emailOriginal, resultado.email());
    assertNotNull(resultado.id());

    verify(repositorioUsuarios).save(any(Usuario.class));
    verify(usuarioPublisher).publicarUsuarioActualizado(any(UsuarioActualizado.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción si ID de usuario es inválido")
  void debeLanzarExcepcionSiIdInvalido() {
    // Act & Assert
    // Pasar null para simular ID inválido a la funcion que ahora espera UUID
    assertThrows(DomainException.class, () -> desvincularDiscordUseCase.execute(null));

    verify(repositorioUsuarios, never()).findById(any(UsuarioId.class));
    verify(repositorioUsuarios, never()).save(any(Usuario.class));
    verify(usuarioPublisher, never()).publicarUsuarioActualizado(any());
  }

  @Test
  @DisplayName("Debe permitir re-vincular Discord después de desvincularlo")
  void debePermitirReVincularDespuesDeDesvincular() {
    // Arrange
    java.util.UUID usuarioId = usuario.getId().value();

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act - Desvincular
    UsuarioResult resultadoDesvinculado = desvincularDiscordUseCase.execute(usuarioId);

    // Assert - Discord desvinculado
    assertNull(resultadoDesvinculado.discordUserId());

    // Act - Re-vincular
    usuario.linkDiscord(DiscordUserId.of("999999999"));

    // Assert - Puede vincular nuevamente
    assertFalse(usuario.getDiscordUserId().isEmpty());
    assertEquals("999999999", usuario.getDiscordUserId().value());
  }
}
