package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.dto.VincularDiscordCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.application.usecases.discord.VincularDiscordUseCase;
import com.gamelisto.usuarios.domain.events.UsuarioActualizado;
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
class VincularDiscordUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;
  @Mock private IUsuarioPublisher usuarioPublisher;

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
    VincularDiscordCommand command =
        new VincularDiscordCommand(usuario.getId().value(), "123456789");

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.findByDiscordUserId(any(DiscordUserId.class)))
        .thenReturn(Optional.empty());
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UsuarioResult resultado = vincularDiscordUseCase.execute(command);

    assertNotNull(resultado);
    assertEquals("123456789", resultado.discordUserId());

    ArgumentCaptor<UsuarioActualizado> eventCaptor =
        ArgumentCaptor.forClass(UsuarioActualizado.class);
    verify(usuarioPublisher).publicarUsuarioActualizado(eventCaptor.capture());
    assertEquals("123456789", eventCaptor.getValue().discordUserId());
  }

  @Test
  @DisplayName("Debe lanzar excepcion si usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    VincularDiscordCommand command =
        new VincularDiscordCommand(
            java.util.UUID.fromString("00000000-0000-0000-0000-000000000000"), "123456789");

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.empty());

    assertThrows(ApplicationException.class, () -> vincularDiscordUseCase.execute(command));
    verify(usuarioPublisher, never()).publicarUsuarioActualizado(any());
  }

  @Test
  @DisplayName("Debe lanzar excepcion si Discord ya esta vinculado a otro usuario")
  void debeLanzarExcepcionSiDiscordYaVinculadoAOtroUsuario() {
    Usuario otroUsuario =
        Usuario.create(
            Username.of("otheruser"), Email.of("other@test.com"), PasswordHash.of("$2a$10$hash"));

    VincularDiscordCommand command =
        new VincularDiscordCommand(usuario.getId().value(), "123456789");

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.findByDiscordUserId(any(DiscordUserId.class)))
        .thenReturn(Optional.of(otroUsuario));

    assertThrows(ApplicationException.class, () -> vincularDiscordUseCase.execute(command));
    verify(usuarioPublisher, never()).publicarUsuarioActualizado(any());
  }

  @Test
  @DisplayName("Debe permitir vincular Discord si ya pertenece al mismo usuario")
  void debePermitirVincularSiYaEstaVinculadoAlMismoUsuario() {
    usuario.linkDiscord(DiscordUserId.of("123456789"));

    VincularDiscordCommand command =
        new VincularDiscordCommand(usuario.getId().value(), "123456789");

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.findByDiscordUserId(any(DiscordUserId.class)))
        .thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UsuarioResult resultado = vincularDiscordUseCase.execute(command);

    assertEquals("123456789", resultado.discordUserId());
    verify(usuarioPublisher).publicarUsuarioActualizado(any(UsuarioActualizado.class));
  }

  @Test
  @DisplayName("Debe actualizar el Discord User ID si ya estaba vinculado")
  void debeActualizarDatosDiscordSiYaEstabaVinculado() {
    usuario.linkDiscord(DiscordUserId.of("111111111"));

    VincularDiscordCommand command =
        new VincularDiscordCommand(usuario.getId().value(), "222222222");

    when(repositorioUsuarios.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.findByDiscordUserId(any(DiscordUserId.class)))
        .thenReturn(Optional.empty());
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UsuarioResult resultado = vincularDiscordUseCase.execute(command);

    assertEquals("222222222", resultado.discordUserId());
    verify(usuarioPublisher).publicarUsuarioActualizado(any(UsuarioActualizado.class));
  }
}
