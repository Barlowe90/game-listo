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
import com.gamelisto.usuarios.domain.usuario.DiscordUserId;
import com.gamelisto.usuarios.domain.usuario.Email;
import com.gamelisto.usuarios.domain.usuario.PasswordHash;
import com.gamelisto.usuarios.domain.usuario.Usuario;
import com.gamelisto.usuarios.domain.usuario.UsuarioId;
import com.gamelisto.usuarios.domain.usuario.Username;
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
    usuario.linkDiscord(DiscordUserId.of("123456789"));
  }

  @Test
  @DisplayName("Debe desvincular cuenta de Discord exitosamente")
  void debeDesvincularDiscordExitosamente() {
    java.util.UUID usuarioId = usuario.getId().value();

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UsuarioResult resultado = desvincularDiscordUseCase.execute(usuarioId);

    assertNotNull(resultado);
    assertNull(resultado.discordUserId());

    ArgumentCaptor<UsuarioActualizado> eventCaptor =
        ArgumentCaptor.forClass(UsuarioActualizado.class);
    verify(usuarioPublisher).publicarUsuarioActualizado(eventCaptor.capture());
    assertNull(eventCaptor.getValue().discordUserId());
  }

  @Test
  @DisplayName("Debe lanzar excepcion si usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    java.util.UUID usuarioId = java.util.UUID.fromString("00000000-0000-0000-0000-000000000000");

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.empty());

    assertThrows(ApplicationException.class, () -> desvincularDiscordUseCase.execute(usuarioId));
    verify(usuarioPublisher, never()).publicarUsuarioActualizado(any());
  }

  @Test
  @DisplayName("Debe funcionar aunque Discord no este vinculado")
  void debeFuncionarAunqueDiscordNoVinculado() {
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

    UsuarioResult resultado = desvincularDiscordUseCase.execute(usuarioId);

    assertNotNull(resultado);
    assertNull(resultado.discordUserId());
  }

  @Test
  @DisplayName("Debe limpiar el Discord User ID al desvincular")
  void debeEliminarTodosDatosDiscord() {
    java.util.UUID usuarioId = usuario.getId().value();

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(
            invocation -> {
              Usuario savedUsuario = invocation.getArgument(0);
              assertTrue(savedUsuario.getDiscordUserId().isEmpty());
              return savedUsuario;
            });

    UsuarioResult resultado = desvincularDiscordUseCase.execute(usuarioId);

    assertNull(resultado.discordUserId());
    verify(usuarioPublisher).publicarUsuarioActualizado(any(UsuarioActualizado.class));
  }

  @Test
  @DisplayName("Debe preservar otros datos del usuario al desvincular")
  void debePreservarOtrosDatosAlDesvincular() {
    java.util.UUID usuarioId = usuario.getId().value();

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UsuarioResult resultado = desvincularDiscordUseCase.execute(usuarioId);

    assertEquals(usuario.getUsername().value(), resultado.username());
    assertEquals(usuario.getEmail().value(), resultado.email());
  }

  @Test
  @DisplayName("Debe lanzar excepcion si el ID de usuario es invalido")
  void debeLanzarExcepcionSiIdInvalido() {
    assertThrows(DomainException.class, () -> desvincularDiscordUseCase.execute(null));
    verify(repositorioUsuarios, never()).findById(any(UsuarioId.class));
  }

  @Test
  @DisplayName("Debe permitir re-vincular Discord despues de desvincularlo")
  void debePermitirReVincularDespuesDeDesvincular() {
    java.util.UUID usuarioId = usuario.getId().value();

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UsuarioResult resultadoDesvinculado = desvincularDiscordUseCase.execute(usuarioId);
    assertNull(resultadoDesvinculado.discordUserId());

    usuario.linkDiscord(DiscordUserId.of("999999999"));

    assertEquals("999999999", usuario.getDiscordUserId().value());
  }
}
