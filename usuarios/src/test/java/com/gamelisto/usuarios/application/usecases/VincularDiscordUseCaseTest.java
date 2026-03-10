package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.dto.VincularDiscordCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VincularDiscordUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;

  @InjectMocks private VincularDiscordUseCase vincularDiscordUseCase;

  private Usuario usuario;

  @BeforeEach
  void setUp() {
    usuario =
        Usuario.create(
            Username.of("testuser"), Email.of("test@test.com"), PasswordHash.of("$2a$10$hash"));
  }

  @Test
  @DisplayName("Debe vincular cuenta de Discord exitosamente")
  void debeVincularDiscordExitosamente() {
    // Arrange
    VincularDiscordCommand command =
        new VincularDiscordCommand(usuario.getId().value(), "123456789", "player#1234");

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.findByDiscordUserId(any(DiscordUserId.class)))
        .thenReturn(Optional.empty());
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UsuarioResult resultado = vincularDiscordUseCase.execute(command);

    // Assert
    assertNotNull(resultado);
    assertEquals("123456789", resultado.discordUserId());
    assertEquals("player#1234", resultado.discordUsername());

    verify(repositorioUsuarios).findById(any(UsuarioId.class));
    verify(repositorioUsuarios).findByDiscordUserId(any(DiscordUserId.class));
    verify(repositorioUsuarios).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción si usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    // Arrange
    VincularDiscordCommand command =
        new VincularDiscordCommand(
            java.util.UUID.fromString("00000000-0000-0000-0000-000000000000"),
            "123456789",
            "player#1234");

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(ApplicationException.class, () -> vincularDiscordUseCase.execute(command));

    verify(repositorioUsuarios).findById(any(UsuarioId.class));
    verify(repositorioUsuarios, never()).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción si Discord ya está vinculado a otro usuario")
  void debeLanzarExcepcionSiDiscordYaVinculadoAOtroUsuario() {
    // Arrange
    Usuario otroUsuario =
        Usuario.create(
            Username.of("otheruser"), Email.of("other@test.com"), PasswordHash.of("$2a$10$hash"));

    VincularDiscordCommand command =
        new VincularDiscordCommand(usuario.getId().value(), "123456789", "player#1234");

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.findByDiscordUserId(any(DiscordUserId.class)))
        .thenReturn(Optional.of(otroUsuario));

    // Act & Assert
    assertThrows(ApplicationException.class, () -> vincularDiscordUseCase.execute(command));

    verify(repositorioUsuarios).findById(any(UsuarioId.class));
    verify(repositorioUsuarios).findByDiscordUserId(any(DiscordUserId.class));
    verify(repositorioUsuarios, never()).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Debe permitir vincular Discord si ya está vinculado al mismo usuario")
  void debePermitirVincularSiYaEstaVinculadoAlMismoUsuario() {
    // Arrange
    usuario.linkDiscord(DiscordUserId.of("123456789"), DiscordUsername.of("player#1234"));

    VincularDiscordCommand command =
        new VincularDiscordCommand(usuario.getId().value(), "123456789", "player#1234");

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.findByDiscordUserId(any(DiscordUserId.class)))
        .thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UsuarioResult resultado = vincularDiscordUseCase.execute(command);

    // Assert
    assertNotNull(resultado);
    assertEquals("123456789", resultado.discordUserId());
    assertEquals("player#1234", resultado.discordUsername());

    verify(repositorioUsuarios).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Debe actualizar datos de Discord si ya estaba vinculado")
  void debeActualizarDatosDiscordSiYaEstabaVinculado() {
    // Arrange
    usuario.linkDiscord(DiscordUserId.of("111111111"), DiscordUsername.of("oldname#0001"));

    VincularDiscordCommand command =
        new VincularDiscordCommand(usuario.getId().value(), "222222222", "newname#9999");

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.findByDiscordUserId(any(DiscordUserId.class)))
        .thenReturn(Optional.empty());
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UsuarioResult resultado = vincularDiscordUseCase.execute(command);

    // Assert
    assertNotNull(resultado);
    assertEquals("222222222", resultado.discordUserId());
    assertEquals("newname#9999", resultado.discordUsername());

    verify(repositorioUsuarios).save(any(Usuario.class));
  }
}
